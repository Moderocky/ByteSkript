package mx.kenzie.skript.runtime.threading;

import mx.kenzie.skript.api.Instruction;
import mx.kenzie.skript.runtime.Skript;

public class OperationController {
    
    protected final AirlockQueue queue;
    protected final SkriptThreadProvider provider;
    protected final Skript skript;
    protected boolean state;
    
    public OperationController(final Skript skript, final SkriptThreadProvider provider) {
        this.queue = new AirlockQueue();
        this.provider = provider;
        this.skript = skript;
        this.skript.getProcesses().add(this);
    }
    
    public synchronized void addInstruction(final Instruction<?> runnable) {
        synchronized (this.queue) {
            this.queue.add(runnable);
        }
    }
    
    public synchronized AirlockQueue getQueue() {
        return queue;
    }
    
    public synchronized void kill() {
        this.skript.getProcesses().remove(this);
    }
    
}
