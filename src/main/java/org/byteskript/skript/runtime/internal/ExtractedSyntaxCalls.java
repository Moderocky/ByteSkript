package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.threading.ScriptThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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
    
    public static Object getListValue(Object key, Object target) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        return list.get(number.intValue());
    }
    
    @SuppressWarnings("unchecked")
    public static void setListValue(Object key, Object target, Object value) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        list.remove(number.intValue());
        list.add(number.intValue(), value);
    }
    
    public static void deleteListValue(Object key, Object target) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        list.remove(number.intValue());
    }
    
    public static Object getMapValue(Object key, Object target) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        return map.get(key);
    }
    
    public static void setMapValue(Object key, Object target, Object value) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        map.put(key, value);
    }
    
    public static void deleteMapValue(Object key, Object target) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        map.remove(key);
    }
}
