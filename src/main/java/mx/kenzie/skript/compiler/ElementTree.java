package mx.kenzie.skript.compiler;

import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.error.ScriptCompileError;

import java.util.Objects;

public final class ElementTree {
    private final SyntaxElement current;
    private final Pattern.Match match;
    private final ElementTree[] nested;
    public boolean compile = true;
    
    public ElementTree(SyntaxElement current, Pattern.Match match, ElementTree... nested) {
        this.current = current;
        this.match = match;
        this.nested = nested;
    }
    
    public void preCompile(Context context) { // Pre-compilation is (outer -> inner)
        context.setCompileCurrent(this);
        try {
            if (compile && current instanceof Section section && !context.isSectionHeader())
                section.preCompileInline(context, match);
            else if (compile) current.preCompile(context, match);
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber(), "Failure during pre-compilation of '" + current.name() + "'", ex);
        }
        for (ElementTree tree : nested) {
            tree.preCompile(context);
        }
    }
    
    public void compile(Context context) { // Post-compilation is (inner -> outer)
        context.setCompileCurrent(this);
        for (ElementTree tree : nested) {
            tree.compile(context);
        }
        try {
            if (compile && current instanceof Section section && !context.isSectionHeader())
                section.compileInline(context, match);
            else if (compile) current.compile(context, match);
        } catch (Throwable ex) {
            throw new ScriptCompileError(context.lineNumber(), "Failure during compilation of '" + current.name() + "'", ex);
        }
    }
    
    public SyntaxElement current() {
        return current;
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
    
    @Override
    public int hashCode() {
        return Objects.hash(current, match, nested);
    }
    
    @Override
    public String toString() {
        return "ElementTree[" +
            "current=" + current + ", " +
            "match=" + match + ", " +
            "nested=" + nested + ']';
    }
    
    
}
