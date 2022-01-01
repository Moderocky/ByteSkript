/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.note.Documentation;
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

@Documentation(
    name = "Continue Loop",
    description = """
        Skips the rest of the current loop and jumps back to the top.
        This starts the next iteration of the loop.
        """,
    examples = {
        """
            loop 10 times:
                print "yes"
                continue loop
                print "never printed"
                """
    }
)
public class ContinueEffect extends Effect {
    
    public ContinueEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "continue [[the ]current] loop");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("continue ")) return null;
        if (!thing.endsWith(" loop")) return null;
        return super.match(thing, context);
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
