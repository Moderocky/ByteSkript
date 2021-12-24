package mx.kenzie.skript.runtime.threading;

import mx.kenzie.skript.runtime.Skript;

import java.util.concurrent.ThreadFactory;

public class SkriptThreadProvider implements ThreadFactory {
    
    volatile int counter;
    private final ScriptExceptionHandler handler = new ScriptExceptionHandler();
    
    public synchronized Thread newThread(final OperationController controller, Runnable runnable, boolean inheritLocals) {
        final Thread thread = new ScriptThread(controller, Skript.THREAD_GROUP, runnable, Skript.THREAD_GROUP.getName() + "-" + counter++, 0, inheritLocals);
        thread.setUncaughtExceptionHandler(handler);
        return thread;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        return this.newThread(new OperationController(Skript.currentInstance(), this), r, false);
    }
    
    public Thread.UncaughtExceptionHandler getHandler() {
        return handler;
    }
    
}
