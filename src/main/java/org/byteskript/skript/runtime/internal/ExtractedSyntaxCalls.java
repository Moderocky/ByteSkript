/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.mirror.MethodAccessor;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.UnsafeAccessor;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.DataList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Ignore
public class ExtractedSyntaxCalls extends UnsafeAccessor {
    
    public static ModifiableCompiler getCompiler() {
        return findInstance().getCompiler();
    }
    
    private static Skript findInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) return Skript.currentInstance();
        return thread.skript;
    }
    
    public static Object convert(Object from, Object object) {
        if (!(object instanceof Class<?> to)) throw new ScriptRuntimeError("Object must be converted to a type.");
        if (to.isAssignableFrom(from.getClass())) return to.cast(from);
        final Converter converter = findInstance().getConverter(from.getClass(), to);
        if (converter == null) return from;
        return converter.convert(from);
    }
    
    public static void handleTestError(Throwable throwable) {
        if (isTest()) UNSAFE.throwException(throwable);
    }
    
    public static boolean isTest() {
        return "true".equals(System.getProperty("skript.test_mode"));
    }
    
    public static void setTest(boolean boo) {
        System.setProperty("skript.test_mode", boo + "");
    }
    
    public static DataList getLoadedScripts() {
        final DataList list = new DataList();
        for (final Script script : findInstance().getScripts()) {
            list.add(script.mainClass());
        }
        return list;
    }
    
    public static String getSystemInput() throws Throwable {
        final Instruction<String> instruction = new Instruction<>() {
            private String value;
            
            @Override
            public String get() {
                return value;
            }
            
            @Override
            public void run() throws Throwable {
                value = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
        };
        runOnMainThread(instruction);
        return instruction.get();
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
    
    public static String readSystemInput() throws Throwable {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    
    public static void runOnAsyncThread(final Runnable runnable) {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot create background process from non-script thread.");
        thread.skript.runOnAsyncThread(runnable);
    }
    
    public static void runOnAsyncThread(final Instruction<?> runnable) {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot create background process from non-script thread.");
        thread.skript.runOnAsyncThread(runnable);
    }
    
    public static Object getListSize(Object target) {
        if (target instanceof Collection list) return list.size();
        if (target instanceof Object[] list) return list.length;
        if (target instanceof Map map) return map.size();
        throw new ScriptRuntimeError("The given collection must be a list.");
    }
    
    public static Object getListValue(Object key, Object target) {
        if (target instanceof Map) return getMapValue(key, target);
        final Number number = Skript.convert(key, Number.class);
        if (target instanceof List list) return list.get(number.intValue());
        if (target instanceof Object[] list) return list[number.intValue()];
        throw new ScriptRuntimeError("The given collection must be a list.");
    }
    
    @SuppressWarnings("unchecked")
    public static void setListValue(Object key, Object target, Object value) {
        if (target instanceof Map) setMapValue(key, target, value);
        final Number number = Skript.convert(key, Number.class);
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
        if (target instanceof Map) deleteMapValue(key, target);
        final Number number = Skript.convert(key, Number.class);
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
