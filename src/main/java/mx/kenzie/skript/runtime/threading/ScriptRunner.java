package mx.kenzie.skript.runtime.threading;

import mx.kenzie.skript.runtime.internal.CompiledScript;

public interface ScriptRunner extends Runnable {
    
    @Override
    default void run() {
        this.start();
        final ScriptThread thread = (ScriptThread) Thread.currentThread();
        thread.controller.kill();
    }
    
    Class<? extends CompiledScript> owner();
    
    void start();
    
}
