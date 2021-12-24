/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.automatic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Method;

public final class GeneratedEffect extends Effect {
    
    private final Method target;
    
    public GeneratedEffect(Library provider, final Method target, String... patterns) {
        super(provider, StandardElements.EFFECT, patterns);
        this.target = target;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        this.writeCall(method, target, context);
        if (target.getReturnType() != void.class) method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
}
