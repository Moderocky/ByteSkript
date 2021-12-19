package mx.kenzie.skript.runtime.internal;

import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import mx.kenzie.skript.api.Instruction;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;

public class Member {
    
    protected MethodAccessor<?> invoker;
    protected MethodAccessor<?> verifier;
    protected final Script script;
    protected final boolean async;
    protected int parameters;
    
    public Member(Script script, Method method, boolean async) {
        this.script = script;
        this.async = async;
        this.parameters = method.getParameterCount();
        final Mirror<?> mirror = Mirror.of(script.mainClass());
        this.invoker = mirror.method(method);
        this.verifier = mirror.method(method.getName() + "_verify", method.getParameterTypes());
    }
    
    public Script getScript() {
        return script;
    }
    
    public Object invoke(Object... arguments) {
        return invoker.invoke(arguments);
    }
    
    public void verify() {
        if (verifier == null) return;
        verifier.invoke(new Object[parameters]);
    }
    
    public static Object runAsync(Object thing, Object args) {
        final Object[] arguments;
        if (args instanceof Collection<?> collection) arguments = collection.toArray();
        else if (args instanceof Object[] array) arguments = array;
        else arguments = new Object[]{args};
        Skript.runOnAsyncThread((Instruction<?>) () -> {
            if (thing instanceof Method method)
                method.invoke(null, arguments);
            else if (thing instanceof Member method)
                method.invoke(arguments);
            else if (thing instanceof MethodAccessor<?> method)
                method.invoke(arguments);
        });
        return null;
    }
    
    public static Object runAsync(Object thing) {
        Skript.runOnAsyncThread((Instruction<?>) () -> {
            if (thing instanceof Method method)
                method.invoke(null);
            else if (thing instanceof Member method)
                method.invoke();
            else if (thing instanceof MethodAccessor<?> method)
                method.invoke();
            else if (thing instanceof Runnable runnable)
                runnable.run();
            else if (thing instanceof Future future)
                future.get();
        });
        return null;
    }
    
    public static MethodAccessor<Object> getFunction(Object owner, String name, Number arguments) {
        final Class<?>[] parameters = new Class[arguments.intValue()];
        Arrays.fill(parameters, Object.class);
        return Mirror.of(owner).method(name, parameters);
    }
    
    public static MethodAccessor<Object> getFunction(Object owner, String name) {
        return Mirror.of(owner).method(name);
    }
    
}
