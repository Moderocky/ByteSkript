package mx.kenzie.skript.error;

public class ScriptLibraryError extends Error {
    
    public ScriptLibraryError() {
        super();
    }
    
    public ScriptLibraryError(String message) {
        super(message);
    }
    
    public ScriptLibraryError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ScriptLibraryError(Throwable cause) {
        super(cause);
    }
    
    protected ScriptLibraryError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    
}
