package mx.kenzie.skript.runtime.threading;

import mx.kenzie.skript.runtime.Skript;

import java.util.concurrent.ThreadFactory;

public class SkriptThreadProvider implements ThreadFactory {
    
    int counter;
    private final ScriptExceptionHandler handler = new ScriptExceptionHandler();
    
    public Thread newThread(final OperationController controller, Runnable runnable, boolean inheritLocals) {
        final Thread thread = new ScriptThread(controller, Skript.THREAD_GROUP, runnable, Skript.THREAD_GROUP.getName() + "-" + counter++, 0, inheritLocals);
        thread.setUncaughtExceptionHandler(getHandler());
        return thread;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        return newThread(new OperationController(Skript.currentInstance(), this), r, true);
    }
    
    public Thread.UncaughtExceptionHandler getHandler() {
        return handler;
    }
    
}
