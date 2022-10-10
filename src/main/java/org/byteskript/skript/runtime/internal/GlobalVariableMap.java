/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import org.byteskript.skript.runtime.Skript;

@Description("""
    The map that holds global variables.
    
    This is thread-safe.
    """)
public class GlobalVariableMap extends VariableMap {
    
    protected transient final Object lock = new Object();
    
    public static void deleteVariable(Object name) {
        if (name == null) return;
        final GlobalVariableMap map = Skript.getVariables();
        synchronized (map.lock) {
            map.remove(name + "");
        }
    }
    
    public static void addVariable(Object name, Object value) {
        if (name == null) return;
        if (value == null) return;
        final GlobalVariableMap map = Skript.getVariables();
        synchronized (map.lock) {
            final Object original = map.get(name + "");
            map.put(name + "", OperatorHandler.addObject(value, original));
        }
    }
    
    public static Object getVariable(Object name) {
        if (name == null) return null;
        final GlobalVariableMap map = Skript.getVariables();
        synchronized (map.lock) {
            return map.get(name + "");
        }
    }
    
    public static void setVariable(Object name, Object value) {
        if (name == null) return;
        final GlobalVariableMap map = Skript.getVariables();
        synchronized (map.lock) {
            if (value == null) map.remove(name + "");
            else map.put(name + "", value);
        }
    }
    
    public static void removeVariable(Object name, Object value) {
        if (name == null) return;
        if (value == null) return;
        final GlobalVariableMap map = Skript.getVariables();
        synchronized (map.lock) {
            final Object original = map.get(name + "");
            map.put(name + "", OperatorHandler.subtract(original, value));
        }
    }
    
}
