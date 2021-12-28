/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

public class BreakIfEffect extends Effect {
    
    public BreakIfEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(break|exit) [[the ]current] section if %Boolean%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" section if ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ProgrammaticSplitTree tree = context.getCurrentTree();
        if (tree == null)
            throw new ScriptCompileError(context.lineNumber(), "Not in a breakable section.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label end = tree.getEnd().use();
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(157, end));
        context.setState(CompileState.CODE_BODY);
    }
    
}
