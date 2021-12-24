package org.byteskript.skript.app;

import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Instruction;
import org.byteskript.skript.runtime.threading.AirlockQueue;
import org.byteskript.skript.runtime.threading.OperationController;

public final class SimpleThrottleController extends Thread implements Runnable {
    private final Skript skript;
    
    public SimpleThrottleController(Skript skript) {
        this.skript = skript;
    }
    
    @Override
    public void run() {
        while (true) {
            for (final OperationController process : skript.getProcesses()) {
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
    
}
