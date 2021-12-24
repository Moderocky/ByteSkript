/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.threading.ScriptThread;

import java.util.HashMap;

public class ThreadVariableMap extends HashMap<String, Object> {
    
    public static Object getVariable(Object name) {
        if (name == null) return null;
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        return thread.variables.get(name + "");
    }
    
    public static void setVariable(Object name, Object value) {
        if (name == null) return;
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (value == null) thread.variables.remove(name + "");
        else thread.variables.put(name + "", value);
    }
    
    public static void deleteVariable(Object name) {
        if (name == null) return;
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        thread.variables.remove(name + "");
    }
    
}
