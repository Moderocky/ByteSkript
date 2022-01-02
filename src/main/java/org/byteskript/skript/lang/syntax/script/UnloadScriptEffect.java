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

import java.io.IOException;

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
public class UnloadScriptEffect extends Effect {
    
    public UnloadScriptEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "unload script %Object%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "unloadScript", Object.class));
    }
    
    @ForceExtract
    public static void unloadScript(Object object) throws IOException {
        if (object instanceof Class main)
            Skript.currentInstance().unloadScript(main);
        else if (object instanceof Script script)
            Skript.currentInstance().unloadScript(script);
        else if (object instanceof String name)
            Skript.currentInstance().unloadScript(Skript.LOADER.findClass(name.replace('/', '.')));
    }
    
}
