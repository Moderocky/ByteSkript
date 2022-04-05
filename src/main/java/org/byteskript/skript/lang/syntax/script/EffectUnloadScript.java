/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.script;

import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;

@Documentation(
    name = "Unload Script",
    description = """
        Unloads a script by its class name.
        If you store the script or any of its functions it will not unload.
        This is a **very** destructive operation - misusing it can cause serious errors.
        """,
    examples = {
        """
            unload script skript/myscript
            """
    }
)
public class EffectUnloadScript extends Effect {
    
    public EffectUnloadScript() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "unload script %Object%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "unloadScript", Object.class));
    }
    
    @ForceExtract
    public static void unloadScript(Object object) {
        if (object instanceof Class main)
            Skript.localInstance().unloadScript(main);
        else if (object instanceof Script script)
            Skript.localInstance().unloadScript(script);
        else if (object instanceof String name)
            Skript.localInstance().unloadScript(Skript.localLoader().findClass(name.replace('/', '.')));
    }
    
}
