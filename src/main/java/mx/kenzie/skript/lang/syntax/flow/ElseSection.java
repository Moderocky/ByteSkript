package mx.kenzie.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.IfElseTree;
import mx.kenzie.skript.compiler.structure.SectionMeta;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

public class ElseSection extends Section {
    
    public ElseSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "else");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        if (!(context.getTree(context.getSection(1)) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Else used without preceding if-section.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = tree.getNext();
        method.writeCode((writer, visitor) -> visitor.visitLabel(next));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("else")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        if (!(context.getTree(context.getSection()) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance if/else flow tree.");
        context.setState(CompileState.CODE_BODY);
        tree.close(context);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Else' must be used as section-header.");
    }
    
}
