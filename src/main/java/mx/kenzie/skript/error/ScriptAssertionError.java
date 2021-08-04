package mx.kenzie.skript.error;

public class ScriptAssertionError extends AssertionError {
    
    public ScriptAssertionError() {
        super();
    }
    
    public ScriptAssertionError(Object detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(boolean detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(char detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(int detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(long detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(float detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(double detailMessage) {
        super(detailMessage);
    }
    
    public ScriptAssertionError(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
