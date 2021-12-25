/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

public class ReturnEffect extends Effect {
    
    public ReturnEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "return %Object%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Type type = method.getErasure().returnType();
        if (!type.equals(CommonTypes.OBJECT)) method.writeCode(WriteInstruction.cast(type));
        method.writeCode(WriteInstruction.returnObject());
        context.setState(CompileState.CODE_BODY);
    }
    
}
