package mx.kenzie.skript.lang.syntax.event;

import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.syntax.EventHolder;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.event.Load;

public class LoadEvent extends EventHolder {
    
    public LoadEvent() {
        super(SkriptLangSpec.LIBRARY, "on [script ]load");
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return Load.class;
    }
    
}
