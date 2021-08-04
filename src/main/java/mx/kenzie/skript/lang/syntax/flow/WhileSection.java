package mx.kenzie.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.WhileTree;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

public class WhileSection extends Section {
    
    public WhileSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "while %Boolean%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final WhileTree tree = new WhileTree(context.getSection());
        context.createTree(tree);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label top = new Label();
        tree.setTop(top);
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection()) instanceof WhileTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label end = tree.getEnd().use();
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, end));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("while ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context) {
        if (!(context.getTree(context.getSection()) instanceof WhileTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance while flow tree.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        final Label top = tree.getTop();
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(167, top));
        method.writeCode(tree.getEnd().instruction());
    }
    
}
