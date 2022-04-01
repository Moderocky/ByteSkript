/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.timing;

import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.time.Duration;

@Documentation(
    name = "Wait",
    description = """
        Waits for the specified duration.
        This process is halted during this time. Background processes will continue.
        """,
    examples = {
        """
            wait 10 seconds
            wait 50 milliseconds
                """
    }
)
public class WaitEffect extends Effect {
    
    public WaitEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wait %Duration%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @ForceExtract
    public static void run(Object object) {
        if (!(object instanceof Duration duration))
            throw new ScriptRuntimeError("Wait effect requires duration.");
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            throw new ScriptRuntimeError("Wait effect interrupted.", ex);
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.startsWith("wait for ")) return null;
        if (!thing.startsWith("wait ")) return null;
        return super.match(thing, context);
    }
}
