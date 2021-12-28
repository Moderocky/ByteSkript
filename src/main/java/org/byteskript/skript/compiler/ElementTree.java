/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.*;

public final class ElementTree {
    private final SyntaxElement current;
    private final Pattern.Match match;
    private ElementTree[] nested;
    public boolean compile = true;
    public boolean treasure = false;
    public boolean takeAtomic = false;
    public HandlerType type = StandardHandlers.GET;
    
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
    public String toString() {
        return "ElementTree{" +
            "current=" + current.name() +
            ", nested=" + Arrays.toString(nested) +
            ", compile=" + compile +
            '}';
    }
}
