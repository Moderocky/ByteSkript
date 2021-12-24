/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.runtime.Skript;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalVariableMap extends ConcurrentHashMap<String, Object> {
    
    public static Object getVariable(Object name) {
        if (name == null) return null;
        return Skript.getVariables().get(name + "");
    }
    
    public static void setVariable(Object name, Object value) {
        if (name == null) return;
        if (value == null) Skript.getVariables().remove(name + "");
        else Skript.getVariables().put(name + "", value);
    }
    
    public static void deleteVariable(Object name) {
        if (name == null) return;
        Skript.getVariables().remove(name + "");
    }
    
}
