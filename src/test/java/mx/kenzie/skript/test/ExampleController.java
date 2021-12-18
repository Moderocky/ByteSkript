package mx.kenzie.skript.test;

import mx.kenzie.skript.api.Instruction;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.threading.AirlockQueue;
import mx.kenzie.skript.runtime.threading.OperationController;

public record ExampleController(Skript skript) implements Runnable {
    
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
    
}
