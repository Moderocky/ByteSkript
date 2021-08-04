package mx.kenzie.skript.api;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

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
