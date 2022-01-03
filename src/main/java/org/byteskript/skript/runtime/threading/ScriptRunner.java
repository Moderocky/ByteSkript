/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.internal.CompiledScript;

/**
 * A task designed to start the script-running process.
 */
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
