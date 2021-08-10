package mx.kenzie.skript.api.automatic;

import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.syntax.EventHolder;

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
