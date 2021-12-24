package org.byteskript.skript.error;

public class ScriptBootstrapError extends Error {
    
    public ScriptBootstrapError() {
        super();
    }
    
    public ScriptBootstrapError(String message) {
        super(message);
    }
    
    public ScriptBootstrapError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ScriptBootstrapError(Throwable cause) {
        super(cause);
    }
    
    protected ScriptBootstrapError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
