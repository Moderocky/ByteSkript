/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

public class ScriptCompileError extends Error implements ScriptError {
    
    private final boolean fill = true;
    private final int line;
    
    public int getLine() {
        return line;
    }
    
    public ScriptCompileError(int line) {
        super();
        this.line = line;
    }
    
    public ScriptCompileError(int line, String message) {
        super(message);
        this.line = line;
    }
    
    public ScriptCompileError(int line, String message, Throwable cause) {
        super(message, cause);
        this.line = line;
    }
    
    public ScriptCompileError(int line, Throwable cause) {
        super(cause);
        this.line = line;
    }
    
    protected ScriptCompileError(int line, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.line = line;
    }
    
    @Override
    public String toString() {
        String s = getClass().getSimpleName();
        String message = getLocalizedMessage();
        return ((message != null) ? (s + ": " + message) : s) + " (Line " + line + ")";
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        if (fill && System.getProperty("debug_mode") != null)
            return super.fillInStackTrace();
        return this;
    }
}
