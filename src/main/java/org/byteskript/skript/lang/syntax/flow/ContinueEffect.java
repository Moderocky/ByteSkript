package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.LoopTree;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
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
