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
import org.byteskript.skript.runtime.Skript;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Documentation(
    name = "Load Script",
    description = """
        Loads a script from the given file or string source code.
        Not all installations can load scripts after start-up: check whether the compiler exists first.
        """,
    examples = {
        """
            load script {file} as "skript/myscript"
            load script {code} as "skript/myscript"
                """
    }
)
public class EffectLoadScript extends Effect {
    
    public EffectLoadScript() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "load script %Object% as %String%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "loadScript", Object.class, String.class));
    }
    
    @ForceExtract
    @SuppressWarnings("deprecation")
    public static void loadScript(Object object, String name) throws IOException {
        if (object instanceof File file)
            Skript.localInstance().compileLoad(file, name.replace('/', '.'));
        else if (object instanceof String string)
            Skript.localInstance()
                .compileLoad(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)), name.replace('/', '.'));
    }
    
}
