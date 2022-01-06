/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Instruction;

/**
 * This is the runtime half of the airlock system.
 * A distribution needs to provide the other half, which is the throttle controller.
 * <p>
 * Together, these two halves bind multiple threads to the 'main' thread.
 * This allows for the behaviour defined in the language specification.
 */
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
    
    public Skript getSkript() {
        return skript;
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
