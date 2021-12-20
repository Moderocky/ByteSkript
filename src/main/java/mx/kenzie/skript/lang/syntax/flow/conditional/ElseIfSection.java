package mx.kenzie.skript.lang.syntax.flow.conditional;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
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

public class ElseIfSection extends Section {
    
    public ElseIfSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "else if %Boolean%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Else-if used without preceding if-section.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = new Label();
        tree.setNext(next);
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, next));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("else if ")) return null;
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
        final MethodBuilder method = context.getMethod();
        final Label label = tree.getNext();
        final Label end = tree.getEnd().use();
        method.writeCode((writer, visitor) -> {
            visitor.visitJumpInsn(167, end);
            visitor.visitLabel(label);
        });
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Else-if' must be used as section-header.");
    }
    
}
