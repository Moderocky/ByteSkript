package mx.kenzie.skript.runtime.threading;

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
    
    public synchronized void swap() {
        this.state = state ^ true;
    }
    
    public synchronized void addInstruction(final Runnable runnable) {
        synchronized (this.queue) {
            this.queue.add(runnable);
        }
        while (true) {
            synchronized (this.queue) {
                try {
                    if (queue.isEmpty()) break;
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    
    public synchronized AirlockQueue getQueue() {
        return queue;
    }
    
    public synchronized boolean empty() {
        synchronized (this.queue) {
            return this.queue.isEmpty();
        }
    }
    
    public synchronized void kill() {
        this.skript.getProcesses().remove(this);
    }
    
}
