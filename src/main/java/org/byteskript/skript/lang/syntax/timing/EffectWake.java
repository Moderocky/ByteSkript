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
import org.byteskript.skript.runtime.threading.ScriptThread;

@Documentation(
    name = "Wake",
    description = """
        Wakes the given process, if it is sleeping.
        This is useful for building multi-process programs.
        """,
    examples = {
        """
            set {thread} to the current process
            run a new runnable in the background:
                wait 10 seconds
                wake {thread}
            sleep
            print "something woke me"
                """
    }
)
public class EffectWake extends Effect {
    
    public EffectWake() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wake [up] %Thread%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @ForceExtract
    public static void run(Object object) {
        if (!(object instanceof Thread))
            throw new ScriptRuntimeError("Wake effect requires thread process object.");
        if (!(object instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot wake non-script thread.");
        synchronized (thread.lock) {
            thread.lock.notify();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("wake ")) return null;
        return super.match(thing, context);
    }
}
