package mx.kenzie.skript.compiler;

import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.util.*;

public final class ElementTree {
    private final SyntaxElement current;
    private final Pattern.Match match;
    private ElementTree[] nested;
    public boolean compile = true;
    public boolean takeAtomic = false;
    public HandlerType type = StandardHandlers.GET;
    
    public ElementTree(SyntaxElement current, Pattern.Match match, ElementTree... nested) {
        this.current = current;
        this.match = match;
        this.nested = nested;
    }
    
    public void preCompile(Context context) { // Pre-compilation is (outer -> inner)
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
        var that = (ElementTree) obj;
        return Objects.equals(this.current, that.current) &&
            Objects.equals(this.match, that.match) &&
            Objects.equals(this.nested, that.nested);
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
            "current=" + current.getClass().getSimpleName() + ", " +
            "nested=" + Arrays.toString(nested) + '}';
    }
    
    
}
