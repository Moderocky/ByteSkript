/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

public class ScriptParseError extends Error {
    
    private final int line;
    
    public ScriptParseError(int line) {
        super();
        this.line = line;
    }
    
    public ScriptParseError(int line, String message) {
        super(message);
        this.line = line;
    }
    
    public ScriptParseError(int line, String message, Throwable cause) {
        super(message, cause);
        this.line = line;
    }
    
    public ScriptParseError(int line, Throwable cause) {
        super(cause);
        this.line = line;
    }
    
    protected ScriptParseError(int line, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.line = line;
    }

//    @Override
//    public Throwable fillInStackTrace() {
//        // no need for stack trace
//        return this;
//    }

}
