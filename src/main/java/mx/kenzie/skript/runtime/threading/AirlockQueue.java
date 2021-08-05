package mx.kenzie.skript.runtime.threading;

import java.util.concurrent.SynchronousQueue;

public class AirlockQueue extends SynchronousQueue<Runnable> {
    
    public AirlockQueue() {
        super();
    }
    
    public AirlockQueue(boolean fair) {
        super(fair);
    }
    
}
