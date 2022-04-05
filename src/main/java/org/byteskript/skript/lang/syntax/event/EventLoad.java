/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.event;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.EventHolder;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.runtime.event.Load;

@Documentation(
    name = "Load",
    description = """
        Run when this script loads.""",
    examples = {
        """
            on script load:
                trigger:
                    print "this script loaded"
                    """
    }
)
public class EventLoad extends EventHolder {
    
    public EventLoad() {
        super(SkriptLangSpec.LIBRARY, "on [script] load");
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return Load.LoadThis.class;
    }
    
}
