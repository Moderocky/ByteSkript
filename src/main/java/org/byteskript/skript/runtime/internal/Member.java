/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.threading.ScriptRunner;
import org.byteskript.skript.runtime.threading.ScriptThread;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;

@Description("""
    A handle for a function, event, etc. inside a script.
    
    Keeping strong references to this is not advised, since it will prevent safe unloading.
    The member can be triggered safely from this.
    """)
public class Member {
    
    protected final Script script;
    protected final boolean async;
    protected MethodAccessor<?> invoker;
    protected MethodAccessor<?> verifier;
    protected int parameters;
    
    @Ignore
    public Member(Script script, Method method, boolean async) {
        this.script = script;
        this.async = async;
        this.parameters = method.getParameterCount();
        final Mirror<?> mirror = Mirror.of(script.mainClass()).useProvider(script.skriptInstance().getLoader());
        this.invoker = mirror.method(method);
        this.verifier = mirror.method(method.getName() + "_verify", method.getParameterTypes());
    }
    
    @Ignore
    public static Object runAsync(Object thing, Object args) {
        final Object[] arguments;
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot create background process from non-script thread.");
        if (args instanceof Collection<?> collection) arguments = collection.toArray();
        else if (args instanceof Object[] array) arguments = array;
        else arguments = new Object[]{args};
        thread.skript.runOnAsyncThread((Instruction<?>) () -> {
            if (thing instanceof Method method) method.invoke(null, arguments);
            else if (thing instanceof Member method) method.invoke(arguments);
            else if (thing instanceof MethodAccessor<?> method) method.invoke(arguments);
        });
        return null;
    }
    
    @Description("""
        Invokes this member directly.
        
        This will not run on a script thread, so may not function properly.
        See [run](method:run(1)) for the safe invocation method.
        """)
    public Object invoke(Object... arguments) {
        return invoker.invoke(arguments);
    }
    
    @Ignore
    public static Object runAsync(Object thing) {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot create background process from non-script thread.");
        thread.skript.runOnAsyncThread((Instruction<?>) () -> {
            if (thing instanceof Method method) method.invoke(null);
            else if (thing instanceof Member method) method.invoke();
            else if (thing instanceof MethodAccessor<?> method) method.invoke();
            else if (thing instanceof Runnable runnable) runnable.run();
            else if (thing instanceof Future future) future.get();
        });
        return null;
    }
    
    @Ignore
    public static MethodAccessor<Object> findFunction(Object owner, String name) {
        return Mirror.of(owner).useProvider(Skript.findLoader()).method(name);
    }
    
    @Ignore
    public static MethodAccessor<Object> getFunction(Object source, String pattern) {
        final String clean = pattern.trim();
        final Object owner;
        final int arguments;
        final String name;
        if (clean.contains("(")) {
            final String params = clean.substring(clean.indexOf('(') + 1, clean.indexOf(')')).trim();
            if (params.isEmpty()) arguments = 0;
            else arguments = count(params);
            name = clean.substring(0, clean.indexOf('(')).trim();
        } else {
            arguments = 0;
            if (clean.contains(" ")) name = clean.substring(0, clean.indexOf(' ')).trim();
            else name = clean.trim();
        }
        if (clean.contains(" from ")) {
            final String result = clean.substring(clean.indexOf(" from ") + 6).trim();
            owner = Skript.findAnyClass(result.replace('/', '.'));
        } else owner = source;
        return findFunction(owner, name, arguments);
    }
    
    private static int count(String pattern) {
        int count = 0;
        for (String s : pattern.split(",")) {
            if (s.isEmpty() || s.isBlank()) continue;
            count++;
        }
        return count;
    }
    
    @Ignore
    public static MethodAccessor<Object> findFunction(Object owner, String name, Number arguments) {
        final Class<?>[] parameters = new Class[arguments.intValue()];
        Arrays.fill(parameters, Object.class);
        return Mirror.of(owner).useProvider(Skript.findLoader()).method(name, parameters);
    }
    
    @Description("Gets the script instance that provided this member.")
    public Script getScript() {
        return script;
    }
    
    @Description("""
        Triggers this member in a new script thread.
        This will be run in the runtime from which the member was originally loaded.
        
        This is the safe and correct way to trigger a function from a Java process.
        """)
    public Future<?> run(Object... arguments) {
        final ScriptRunner runner = new ScriptRunner() {
            @Override
            public void start() {
                invoker.invoke(arguments);
            }
            
            @Override
            public Class<? extends CompiledScript> owner() {
                return script.mainClass();
            }
        };
        return script.skriptInstance().runScript(runner);
    }
    
    @Description("""
        Triggers this member in a new script thread.
        This allows running in a different script runtime.
        
        This is potentially unsafe - alternative class versions can bleed between runtimes.
        """)
    public Future<?> run(Skript skript, Object... arguments) {
        final ScriptRunner runner = new ScriptRunner() {
            
            private Object value;
            
            @Override
            public void start() {
                final Object result = invoker.invoke(arguments);
                synchronized (this) {
                    this.value = result;
                }
            }
            
            @Override
            public Object result() {
                synchronized (this) {
                    return value;
                }
            }
            
            @Override
            public Class<? extends CompiledScript> owner() {
                return script.mainClass();
            }
        };
        return skript.runScript(runner);
    }
    
    /**
     * Triggers this member's verifier with a set of `null` parameters.
     * Rather than returning a boolean result, this will throw an error if the verifier fails.
     */
    @Description("""
        Triggers this member's verifier with a set of `null` parameters.
        Rather than returning a boolean result, this will throw an error if the verifier fails.""")
    public void verify() {
        if (verifier == null) return;
        this.verifier.invoke(new Object[parameters]);
    }
    
}
