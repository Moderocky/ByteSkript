/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.entry.Trigger;
import org.byteskript.skript.lang.syntax.variable.VariableExpression;

import java.util.*;

public final class ElementTree {
    public final Map<String, Object> metadata = new HashMap<>();
    private final SyntaxElement current;
    private final Pattern.Match match;
    public boolean compile = true;
    public boolean treasure = false;
    public boolean takeAtomic = false;
    public Type wanted = null;
    public HandlerType type = StandardHandlers.GET;
    private ElementTree[] nested;
    
    public ElementTree(SyntaxElement current, Pattern.Match match, ElementTree... nested) {
        this.current = current;
        this.match = match;
        this.nested = nested;
    }
    
    public ElementTree[] falseCopy() {
        final ElementTree[] trees = new ElementTree[nested.length];
        for (int i = 0; i < nested.length; i++) {
            trees[i] = nested[i].deepCopy();
            trees[i].treasure = true;
        }
        return trees;
    }
    
    public ElementTree shallowCopy() {
        final ElementTree tree = new ElementTree(current, match, nested);
        tree.compile = compile;
        tree.takeAtomic = takeAtomic;
        tree.type = type;
        return tree;
    }
    
    public ElementTree deepCopy() {
        final ElementTree tree = new ElementTree(current, match, nested);
        tree.compile = compile;
        tree.takeAtomic = takeAtomic;
        tree.type = type;
        for (int i = 0; i < nested.length; i++) {
            tree.nested[i] = nested[i].deepCopy();
        }
        return tree;
    }
    
    public void preCompile(Context context) { // Pre-compilation is (outer -> inner)
        if (treasure) return;
        context.setCompileCurrent(this);
        final HandlerType previous = context.getHandlerMode();
        context.setHandlerMode(type);
        try {
            if (compile && current instanceof Section section && !context.isSectionHeader())
                section.preCompileInline(context, match);
            else if (compile) current.preCompile(context, match);
        } catch (ScriptCompileError ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber(), "Failure during pre-compilation of '" + current.name() + "'", ex);
        }
        context.setHandlerMode(previous);
        for (ElementTree tree : nested) {
            tree.preCompile(context);
        }
    }
    
    public void compile(Context context) { // Post-compilation is (inner -> outer)
        if (treasure) return;
        for (ElementTree tree : nested) {
            tree.compile(context);
        }
        context.setCompileCurrent(this);
        final HandlerType previous = context.getHandlerMode();
        context.setHandlerMode(type);
        try {
            if (compile && current instanceof Section section && !context.isSectionHeader())
                section.compileInline(context, match);
            else if (compile) current.compile(context, match);
        } catch (ScriptCompileError ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber(), "Failure during compilation of '" + current.name() + "'", ex);
        }
        context.setHandlerMode(previous);
    }
    
    public void disableCompilation() {
        this.compile = false;
        for (final ElementTree tree : nested) {
            tree.disableCompilation();
        }
    }
    
    public void disableChildren() {
        for (final ElementTree tree : nested) {
            tree.disableCompilation();
        }
    }
    
    public <Type> Type getLiteralValue() {
        if (current instanceof Literal<?> literal) {
            final Object meta = match.meta();
            if (meta instanceof String string)
                return (Type) literal.parse(string);
            return (Type) meta;
        }
        return null;
    }
    
    public SyntaxElement current() {
        return current;
    }
    
    public Collection<SyntaxElement> list() {
        final List<SyntaxElement> list = new ArrayList<>();
        list.add(current);
        for (ElementTree tree : nested) {
            list.addAll(tree.list());
        }
        return list;
    }
    
    public Pattern.Match match() {
        return match;
    }
    
    public ElementTree[] nested() {
        return nested;
    }
    
    public void emptyNest() {
        this.nested = new ElementTree[0];
    }
    
    public void replaceNest(ElementTree[] trees) {
        this.nested = trees;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(current, match, nested);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        final ElementTree that = (ElementTree) obj;
        return Objects.equals(this.current, that.current) &&
            Objects.equals(this.match, that.match) &&
            Arrays.equals(this.nested, that.nested) &&
            this.compile == that.compile;
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(Context context) {
        final StringBuilder builder = new StringBuilder();
        builder.append(current.getClass().getSimpleName());
        builder.append('(');
        if (current instanceof Literal<?>) {
            builder.append(match.matcher().group());
        } else if (current instanceof VariableExpression) {
            builder.append(match.matcher().group("name"));
        } else if (current instanceof TriggerHolder && context != null && context.getLine() == this && context.getMethod() != null) {
            final MethodErasure erasure = context.getMethod().getErasure();
            boolean comma = false;
            for (final Type type : erasure.parameterTypes()) {
                if (comma) builder.append(", ");
                else comma = true;
                builder.append(type.getSimpleName());
            }
        } else {
            boolean comma = false;
            for (final ElementTree tree : nested) {
                if (comma) builder.append(", ");
                builder.append(tree.toString(context));
                comma = true;
            }
        }
        builder.append(')');
        if (context == null) return builder.toString();
        if (context.isSectionHeader() && context.getLine() == this) builder.append(':');
        if (current instanceof Trigger && context.getLine() == this && context.getMethod() != null) {
            final MethodErasure erasure = context.getMethod().getErasure();
            builder.append(" // ");
            builder.append(erasure.name());
            builder.append(erasure.getDescriptor());
        }
        return builder.toString();
    }
}
