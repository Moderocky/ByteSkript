package org.byteskript.skript.runtime.type;

import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.error.ScriptRuntimeError;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicVariable extends AtomicReference<Object> {
    
    public static AtomicVariable wrap(final Object object) {
        if (object == null) return new AtomicVariable();
        if (object instanceof AtomicVariable variable) return variable;
        final AtomicVariable variable = new AtomicVariable();
        variable.set(object);
        return variable;
    }
    
    public static Object unwrap(final Object object) {
        if (object == null) return null;
        if (object instanceof AtomicVariable variable) return variable.getAcquire();
        return object;
    }
    
    @ForceExtract
    public static void set(Object value, Object ref) {
        if (ref instanceof AtomicVariable variable) variable.set(value);
        else throw new ScriptRuntimeError(ref + " is not an atomic variable.");
    }
    
    @ForceExtract
    public static Object get(Object ref) {
        if (ref instanceof AtomicVariable variable) return variable.getAcquire();
        else return ref;
    }
    
    @ForceExtract
    public static void delete(Object ref) {
        if (ref instanceof AtomicVariable variable) variable.set(null);
        else throw new ScriptRuntimeError(ref + " is not an atomic variable.");
    }
    
}
