/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

public class ScriptAssertionError extends AssertionError {
    private final int line;
    private final Class<?> script;
    
    public ScriptAssertionError(Class<?> script, int line) {
        super();
        this.line = line;
        this.script = script;
        
    }
    
    public ScriptAssertionError(Class<?> script, int line, String message) {
        super(message);
        this.line = line;
        this.script = script;
    }
    
    @Override
    public String toString() {
        final String source;
        final String message = getLocalizedMessage();
        if (script == null) source = "";
        else source = script.getName().replace('.', '/');
        if (line < 1) {
            if (script == null) {
                if (message == null)
                    return "Assertion failed. (Unknown source/line)";
                else
                    return "Assertion failed: " + message + " (Unknown source/line)";
            } else {
                if (message == null)
                    return "Assertion failed. (" + source + ")";
                else
                    return "Assertion failed: " + message + " (" + source + ")";
            }
        } else {
            if (script == null) {
                if (message == null)
                    return "Assertion failed. (Line " + line + ")";
                else
                    return "Assertion failed: " + message + " (Line " + line + ")";
            } else {
                if (message == null)
                    return "Assertion failed. (" + source + " line " + line + ")";
                else
                    return "Assertion failed: " + message + " (" + source + " line " + line + ")";
            }
        }
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
