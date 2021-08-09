package mx.kenzie.skript.api;

import mx.kenzie.skript.runtime.Skript;

public abstract class Event {
    
    public final void run(final Skript skript) {
    }
    
    public boolean isAsync() {
        return false;
    }
    
}
