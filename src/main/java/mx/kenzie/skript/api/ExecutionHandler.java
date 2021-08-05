package mx.kenzie.skript.api;

import java.util.concurrent.Future;

public interface ExecutionHandler {
    
    Future<?> execute(Runnable runnable);
    
    Future<?> schedule(Runnable runnable, long millis);
    
}
