/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.mirror.MethodAccessor;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.byteskript.skript.runtime.type.DataList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class ExtractedSyntaxCalls {
    
    public static ModifiableCompiler getCompiler() {
        return Skript.currentInstance().getCompiler();
    }
    
    public static DataList getLoadedScripts() {
        final DataList list = new DataList();
        for (final Script script : Skript.currentInstance().getScripts()) {
            list.add(script.mainClass());
        }
        return list;
    }
    
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
        if (target instanceof List list) return list.get(number.intValue());
        if (target instanceof Object[] list) return list[number.intValue()];
        throw new ScriptRuntimeError("The given collection must be a list.");
    }
    
    @SuppressWarnings("unchecked")
    public static void setListValue(Object key, Object target, Object value) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (target instanceof List list) {
            list.remove(number.intValue());
            list.add(number.intValue(), value);
            return;
        } else if (target instanceof Object[] array) {
            array[number.intValue()] = value;
            return;
        }
        throw new ScriptRuntimeError("The given collection must be a list.");
    }
    
    public static void deleteListValue(Object key, Object target) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (target instanceof List list) {
            list.remove(number.intValue());
            return;
        } else if (target instanceof Object[] array) {
            array[number.intValue()] = null;
            return;
        }
        throw new ScriptRuntimeError("The given collection must be a list.");
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
    
    public static Object run(Object thing)
        throws Throwable {
        if (thing instanceof Method method)
            return method.invoke(null);
        else if (thing instanceof MethodAccessor<?> runnable)
            return runnable.invoke();
        else if (thing instanceof Member runnable)
            return runnable.invoke();
        else if (thing instanceof Runnable runnable) {
            runnable.run();
            return null;
        } else if (thing instanceof Supplier<?> runnable)
            return runnable.get();
        else if (thing instanceof Future future)
            return future.get();
        return thing;
    }
}
