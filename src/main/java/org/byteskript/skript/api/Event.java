package org.byteskript.skript.api;

import org.byteskript.skript.runtime.Skript;

public abstract class Event {
    
    public final void run(final Skript skript) {
    }
    
    public boolean isAsync() {
        return false;
    }
    
}
