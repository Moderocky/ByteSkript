/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.error.ScriptCompileError;
import org.objectweb.asm.Label;

public class WhileTree extends LoopTree {
    
    public WhileTree(SectionMeta owner) {
        super(owner);
    }
    
    @Override
    public void close(Context context) {
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "While block left unclosed.");
        final Label top = this.getTop();
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(167, top));
        super.close(context);
    }
}
