package mx.kenzie.skript.runtime.internal;

import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.threading.ScriptThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExtractedSyntaxCalls {
    
    public static String getSystemInput() throws Throwable {
        final Instruction<String> instruction = new Instruction<>() {
            private String value;
            
            @Override
            public void run() throws Throwable {
                value = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            
            @Override
            public String get() {
                return value;
            }
        };
        runOnMainThread(instruction);
        return instruction.get();
    }
    
    public static String readSystemInput() throws Throwable {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    
    public static void runOnMainThread(final Instruction<?> runnable) throws Throwable {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot join main thread from non-script thread.");
        thread.controller.addInstruction(runnable);
        synchronized (thread.controller) {
            thread.controller.wait();
        }
    }
    
    public static void runOnAsyncThread(final Runnable runnable) {
        Skript.runOnAsyncThread(runnable);
    }
    
    public static void runOnAsyncThread(final Instruction<?> runnable) {
        Skript.runOnAsyncThread(runnable);
    }
    
}
