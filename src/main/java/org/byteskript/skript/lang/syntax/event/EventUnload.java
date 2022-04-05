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
import org.byteskript.skript.runtime.event.Unload;

@Documentation(
    name = "Unload",
    description = """
        Run when another script is unloaded.""",
    examples = {
        """
            on script unload:
                trigger:
                    print "a script was unloaded"
                    """
    }
)
public class EventUnload extends EventHolder {
    
    public EventUnload() {
        super(SkriptLangSpec.LIBRARY, "on [any] [script] unload");
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return Unload.class;
    }
    
}
