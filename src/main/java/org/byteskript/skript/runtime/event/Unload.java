/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.event;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.EventValue;
import org.byteskript.skript.runtime.Script;

@Ignore
public class Unload extends Event {
    
    protected final String name, path;
    
    public Unload(Script script) {
        this.name = script.getSimpleName();
        this.path = script.getPath();
    }
    
    @EventValue("name")
    public String getName() {
        return name;
    }
    
    @EventValue("script")
    public String getPath() {
        return path;
    }
    
}
