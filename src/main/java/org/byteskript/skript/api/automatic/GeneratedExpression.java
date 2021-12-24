/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.automatic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Method;

public final class GeneratedExpression extends SimpleExpression {
    
    private final Method target;
    private final Type value;
    
    public GeneratedExpression(Library provider, final Method target, String... patterns) {
        super(provider, StandardElements.EXPRESSION, patterns);
        this.target = target;
        this.value = new Type(target.getReturnType());
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert target.getReturnType() != void.class;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return value;
    }
    
}
