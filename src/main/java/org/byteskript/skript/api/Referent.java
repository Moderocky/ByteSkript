/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

@Description("This syntax is designed to be set or altered in some way.")
public interface Referent extends SyntaxElement {
    
    Type getHolderType();
    
    @Override
    default void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = getHandler(StandardHandlers.GET);
        if (target == null) throw new ScriptCompileError(context.lineNumber(), "Referent has no get handler.");
        if (target.getReturnType() == void.class)
            throw new ScriptCompileError(context.lineNumber(), "Referent get handler must not have a void return.");
        this.writeCall(context.getMethod(), target, context);
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    default boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
}
