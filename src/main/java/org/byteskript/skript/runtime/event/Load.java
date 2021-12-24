/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.event;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.EventValue;
import org.byteskript.skript.runtime.Script;

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
