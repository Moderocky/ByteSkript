package mx.kenzie.skript.api;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.handler.StandardHandlers;

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
        final Method target = getHandler(StandardHandlers.GET);
        if (target == null) throw new ScriptCompileError(context.lineNumber(), "Referent has no get handler.");
        if (target.getReturnType() == void.class)
            throw new ScriptCompileError(context.lineNumber(), "Referent get handler must not have a void return.");
        if (Modifier.isStatic(target.getModifiers()))
            throw new ScriptCompileError(context.lineNumber(), "Referent handler is static.");
        method.writeCode(WriteInstruction.invokeVirtual(target));
        context.setState(CompileState.STATEMENT);
    }
    
}
