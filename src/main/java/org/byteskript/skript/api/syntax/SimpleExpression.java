/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public abstract class SimpleExpression extends Element implements SyntaxElement {
    
    public SimpleExpression(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final Method target = handlers.get(context.getHandlerMode());
        if (target == null) return;
        this.prepareExpectedTypes(context, target);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.GET);
        assert target != null;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
        final Type type = context.getCompileCurrent().wanted;
        if (type != null) method.writeCode(WriteInstruction.cast(type));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
}
