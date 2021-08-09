package mx.kenzie.skript.runtime.threading;

import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.runtime.internal.CompiledScript;

public class ScriptThread extends Thread {
    
    public final AirlockQueue queue;
    public final OperationController controller;
    public Class<? extends CompiledScript> initiator;
    public Event event;
    
    public ScriptThread(final OperationController controller, ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
        this.controller = controller;
        this.queue = controller.queue;
    }
}
