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
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class EventValueExpression extends Element implements SyntaxElement {
    
    public EventValueExpression(final Library provider, final String... patterns) {
        super(provider, StandardElements.EXPRESSION, correct(patterns));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.GET);
        assert target != null;
        assert target.getReturnType() != void.class;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
        final Type type = context.getCompileCurrent().wanted;
        if (type != null) method.writeCode(WriteInstruction.cast(type));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
    protected static String[] correct(final String... strings) {
        final List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add("%Event%-" + string);
        }
        return list.toArray(new String[0]);
    }
    
}
