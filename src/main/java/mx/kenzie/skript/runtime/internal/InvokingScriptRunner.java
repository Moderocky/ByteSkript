package mx.kenzie.skript.runtime.internal;

import mx.kenzie.skript.runtime.threading.ScriptRunner;

public record InvokingScriptRunner(Class<? extends CompiledScript> owner, Member method,
                                   Object... parameters) implements ScriptRunner {
    
    @Override
    public void start() {
        method.invoke(null, parameters);
    }
}
