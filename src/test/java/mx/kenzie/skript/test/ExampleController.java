package mx.kenzie.skript.test;

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
                    if (process.empty()) continue;
                    for (Runnable runnable : queue) {
                        runnable.run();
                    }
                    queue.clear();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
}
