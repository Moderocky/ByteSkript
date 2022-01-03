/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.Skript;

import java.util.concurrent.ThreadFactory;

/**
 * Provides {@link ScriptThread}s for the runtime to use.
 */
public class SkriptThreadProvider implements ThreadFactory {
    
    volatile int counter;
    private final ScriptExceptionHandler handler = new ScriptExceptionHandler();
    private Skript skript;
    
    public SkriptThreadProvider() {
    }
    
    public void setSkriptInstance(Skript skript) {
        this.skript = skript;
    }
    
    public synchronized ScriptThread newThread(final OperationController controller, Runnable runnable, boolean inheritLocals) {
        final ScriptThread thread = new ScriptThread(skript, controller, Skript.THREAD_GROUP, runnable, Skript.THREAD_GROUP.getName() + "-" + counter++, 0, inheritLocals);
        thread.setUncaughtExceptionHandler(handler);
        if (inheritLocals && Thread.currentThread() instanceof ScriptThread current)
            thread.variables.putAll(current.variables); // inherits locals :)
        return thread;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        return this.newThread(new OperationController(this.skript, this), r, false);
    }
    
    public Thread.UncaughtExceptionHandler getHandler() {
        return handler;
    }
    
}
