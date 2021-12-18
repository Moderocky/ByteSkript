package mx.kenzie.skript.runtime.internal;

import mx.kenzie.skript.runtime.threading.ScriptRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record InvokingScriptRunner(Class<? extends CompiledScript> owner, Method method,
                                   Object... parameters) implements ScriptRunner {
    
    @Override
    public void start() {
        try {
            method.invoke(null, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
