package mx.kenzie.skript.lang.event;

import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.note.EventValue;
import mx.kenzie.skript.runtime.Script;

public class Load extends Event {
    
    protected final Script script;
    
    public Load(Script script) {
        this.script = script;
    }
    
    @EventValue("name")
    public String getName() {
        return script.getSimpleName();
    }
    
    @EventValue("script")
    public String getPath() {
        return script.getPath();
    }
    
    public static class LoadThis extends Event {
        
        protected final Script script;
        
        public LoadThis(Script script) {
            this.script = script;
        }
        
        @EventValue("name")
        public String getName() {
            return script.getSimpleName();
        }
        
        @EventValue("script")
        public String getPath() {
            return script.getPath();
        }
        
    }
    
}
