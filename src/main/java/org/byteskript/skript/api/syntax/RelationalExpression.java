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
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

import java.lang.reflect.Method;

public abstract class RelationalExpression extends ComplexExpression implements SyntaxElement {
    
    public RelationalExpression(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null : "Missing target for: " + context.getHandlerMode();
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
        if (!context.getHandlerMode().expectReturn()) return;
        final Type type = context.getCompileCurrent().wanted;
        if (type != null) method.writeCode(WriteInstruction.cast(type));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
}
