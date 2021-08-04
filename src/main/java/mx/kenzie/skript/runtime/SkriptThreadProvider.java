package mx.kenzie.skript.runtime;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class SkriptThreadProvider implements ThreadFactory {
    
    int counter;
    
    public Thread newThread(@NotNull Runnable runnable, boolean inheritLocals) {
        final Thread thread = new Thread(Skript.THREAD_GROUP, runnable, Skript.THREAD_GROUP.getName() + "-" + counter++, 0, inheritLocals);
        thread.setUncaughtExceptionHandler(getHandler());
        return thread;
    }
    
    @Override
    public Thread newThread(@NotNull Runnable r) {
        return newThread(r, true);
    }
    
    public Thread.UncaughtExceptionHandler getHandler() {
        return Thread.getDefaultUncaughtExceptionHandler();
    }
    
}
