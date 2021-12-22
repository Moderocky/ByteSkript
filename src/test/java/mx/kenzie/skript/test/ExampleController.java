package mx.kenzie.skript.test;

import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Instruction;
import mx.kenzie.skript.runtime.threading.AirlockQueue;
import mx.kenzie.skript.runtime.threading.OperationController;

import java.util.Objects;

public final class ExampleController extends Thread implements Runnable {
    private final Skript skript;
    
    public ExampleController(Skript skript) {
        this.skript = skript;
    }
    
    @Override
    public void run() {
        while (true) {
            for (OperationController process : skript.getProcesses()) {
                final AirlockQueue queue;
                synchronized (queue = process.getQueue()) {
                    if (queue.isEmpty()) continue;
                    for (Instruction<?> runnable : queue) {
                        try {
                            runnable.run();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    queue.clear();
                }
                synchronized (process) {
                    process.notifyAll();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    public Skript skript() {
        return skript;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExampleController) obj;
        return Objects.equals(this.skript, that.skript);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(skript);
    }
    
    @Override
    public String toString() {
        return "ExampleController[" +
            "skript=" + skript + ']';
    }
    
    
}
