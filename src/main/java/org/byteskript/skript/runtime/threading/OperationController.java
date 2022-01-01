/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Instruction;

public class OperationController {
    
    protected final AirlockQueue queue;
    protected final SkriptThreadProvider provider;
    protected final Skript skript;
    protected boolean state;
    
    public Skript getSkript() {
        return skript;
    }
    
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
