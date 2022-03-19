/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.autodoc.api.note.Warning;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.util.concurrent.atomic.AtomicReference;

@Description("""
    The `{@var}` atomic variable handler in memory.
    This class provides the reference object, and also the wrapping/unwrapping points.
    
    This should rarely be used by syntax or third-party API - atomics are handled internally.
    Unless explicitly asked for by a method (such as with an `AtomicVariable` parameter)
    the atomic will always be unwrapped before a call.
    """)
public class AtomicVariable extends AtomicReference<Object> {
    
    @Description("""
        Returns the atomic-wrapped version of this object.
        If it is already atomic, nothing will be done to it.
        
        If the value is `null` a new AtomicVariable will be returned.
        """)
    @GenerateExample
    public static AtomicVariable wrap(final Object object) {
        if (object == null) return new AtomicVariable();
        if (object instanceof AtomicVariable variable) return variable;
        final AtomicVariable variable = new AtomicVariable();
        variable.set(object);
        return variable;
    }
    
    @Description("""
        Unwraps the given object, if it is atomic.
        If it is not atomic, the object will be returned untouched.
        """)
    @Warning("This does not respect atomic access - use [get](method:get(1)) if thread-safety is required.")
    public static Object unwrap(final Object object) {
        if (object == null) return null;
        if (object instanceof AtomicReference<?> variable) return variable.get();
        return object;
    }
    
    @Description("""
        Sets the atomic reference to the given value.
        
        If the holder is not atomic, this will throw an error.
        """)
    public static void set(Object value, Object atomic) {
        if (atomic instanceof AtomicVariable variable) variable.set(value);
        else throw new ScriptRuntimeError(atomic + " is not an atomic variable.");
    }
    
    public static void add(Object value, Object atomic) {
        if (!(atomic instanceof AtomicVariable variable))
            throw new ScriptRuntimeError(atomic + " is not an atomic variable.");
        final Object original = variable.get();
        if (original == null) variable.set(value);
        else variable.set(OperatorHandler.add(original, value));
    }
    
    public static void remove(Object value, Object atomic) {
        if (!(atomic instanceof AtomicVariable variable))
            throw new ScriptRuntimeError(atomic + " is not an atomic variable.");
        final Object original = variable.get();
        if (original == null && value instanceof Number) variable.set(OperatorHandler.subtract(0, value));
        else variable.set(OperatorHandler.subtract(original, value));
    }
    
    @Description("""
        Unwraps the atomic reference, respecting atomic access.
        This is thread-safe.
        """)
    public static Object get(Object atomic) {
        if (atomic instanceof AtomicReference<?> variable) return variable.getAcquire();
        else return atomic;
    }
    
    @Description("""
        Empties the atomic reference.
        
        If the holder is not atomic, this will throw an error.
        """)
    public static void delete(Object atomic) {
        if (atomic instanceof AtomicReference<?> variable) variable.set(null);
        else throw new ScriptRuntimeError(atomic + " is not an atomic variable.");
    }
    
}
