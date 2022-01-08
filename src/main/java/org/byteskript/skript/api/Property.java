/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface Property extends Referent {
    
    String name();
    
    Type getPropertyType();
    
    Method getHandler(HandlerType type, Type target);
    
    void addHandler(HandlerType type, Type target, Method handle);
    
    @Override
    default void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = this.getHandler(StandardHandlers.GET);
        if (target == null) throw new ScriptCompileError(context.lineNumber(), "Referent has no get handler.");
        if (target.getReturnType() == void.class)
            throw new ScriptCompileError(context.lineNumber(), "Referent get handler must not have a void return.");
        if (Modifier.isStatic(target.getModifiers()))
            throw new ScriptCompileError(context.lineNumber(), "Referent handler is static.");
        method.writeCode(WriteInstruction.invokeVirtual(target));
        context.setState(CompileState.STATEMENT);
    }
    
}
