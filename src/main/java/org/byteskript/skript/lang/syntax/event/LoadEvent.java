/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.event;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.syntax.EventHolder;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.runtime.event.Load;

public class LoadEvent extends EventHolder {
    
    public LoadEvent() {
        super(SkriptLangSpec.LIBRARY, "on [script] load");
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return Load.LoadThis.class;
    }
    
}
