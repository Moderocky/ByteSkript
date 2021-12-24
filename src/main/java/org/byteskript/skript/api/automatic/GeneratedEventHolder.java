package org.byteskript.skript.api.automatic;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.EventHolder;

public final class GeneratedEventHolder extends EventHolder {
    
    private final Class<? extends Event> owner;
    
    public GeneratedEventHolder(Library provider, Class<? extends Event> owner, String... patterns) {
        super(provider, patterns);
        this.owner = owner;
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return owner;
    }
}
