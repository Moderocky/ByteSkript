/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.error;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.TryCatchTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class TryEffect extends Effect {
    
    public TryEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "try[ to]: %Effect%");
    }
    
    @Override
    public CompileState getSubState() {
        return CompileState.CODE_BODY; // need to run an effect inside this!
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final TryCatchTree tree = new TryCatchTree(context.getSection(1), new MultiLabel());
        context.createTree(tree);
        tree.start(context);
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getCurrentTree() instanceof TryCatchTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Inline 'try' cannot be used on a section header.");
        final Label label = tree.getEnd().use();
        final Label next = tree.getStartCatch();
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Try effect used outside method.");
        context.getMethod().writeCode(((writer, visitor) -> {
            visitor.visitJumpInsn(Opcodes.GOTO, label);
            visitor.visitLabel(next);
        }));
        method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("try")) return null;
        if (!thing.contains(":")) return null;
        if (thing.endsWith(":")) {
            context.getError().addHint(this, "Section headers cannot be used in the 'try-to' effect.");
            return null;
        }
        return super.match(thing, context);
    }
    
}
