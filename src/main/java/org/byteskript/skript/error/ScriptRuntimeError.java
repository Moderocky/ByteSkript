package org.byteskript.skript.error;

public class ScriptRuntimeError extends Error {
    
    public ScriptRuntimeError() {
        super();
    }
    
    public ScriptRuntimeError(String message) {
        super(message);
    }
    
    public ScriptRuntimeError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ScriptRuntimeError(Throwable cause) {
        super(cause);
    }
    
    protected ScriptRuntimeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
