package mx.kenzie.skript.runtime.threading;

public class ScriptThread extends Thread {
    
    public final AirlockQueue queue;
    public final OperationController controller;
    
    public ScriptThread(final OperationController controller, ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
        this.controller = controller;
        this.queue = controller.queue;
    }
}
