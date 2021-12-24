package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.internal.CompiledScript;

public interface ScriptRunner extends Runnable {
    
    @Override
    default void run() {
        this.start();
        final ScriptThread thread = (ScriptThread) Thread.currentThread();
        thread.controller.kill();
        thread.variables.clear();
    }
    
    Class<? extends CompiledScript> owner();
    
    void start();
    
}
