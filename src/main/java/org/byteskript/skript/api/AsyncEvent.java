package org.byteskript.skript.api;

public abstract class AsyncEvent extends Event {
    
    @Override
    public final boolean isAsync() {
        return true;
    }
}
