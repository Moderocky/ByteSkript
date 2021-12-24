package org.byteskript.skript.error;

public class ScriptCompileError extends Error {
    
    private final boolean fill = true;
    private final int line;
    
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
        if (fill)
            return super.fillInStackTrace();
        return this;
    }
}
