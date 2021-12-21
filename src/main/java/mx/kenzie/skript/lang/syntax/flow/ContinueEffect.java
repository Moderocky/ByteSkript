package mx.kenzie.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.LoopTree;
import mx.kenzie.skript.compiler.structure.ProgrammaticSplitTree;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

public class ContinueEffect extends Effect {
    
    public ContinueEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "continue [[the ]current ]loop");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ProgrammaticSplitTree tree = context.getCurrentTree();
        if (tree == null)
            throw new ScriptCompileError(context.lineNumber(), "Not in a section.");
        final LoopTree loop = context.findTree(LoopTree.class);
        if (loop == null)
            throw new ScriptCompileError(context.lineNumber(), "Not in a loop.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label top = loop.getTop();
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(167, top));
        context.setState(CompileState.CODE_BODY);
    }
    
}
