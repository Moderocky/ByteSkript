/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

public class ScriptLibraryError extends Error implements ScriptError {
    
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
