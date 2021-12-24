package org.byteskript.skript.error;

public class ScriptLoadError extends Error {
    
    public ScriptLoadError() {
        super();
    }
    
    public ScriptLoadError(String message) {
        super(message);
    }
    
    public ScriptLoadError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ScriptLoadError(Throwable cause) {
        super(cause);
    }
    
    protected ScriptLoadError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
