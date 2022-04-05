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
    name = "Sleep",
    description = """
        Puts the current process to sleep (indefinitely.)
        Another (background) process can wake it up.
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
public class EffectSleep extends Effect {
    
    public EffectSleep() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(sleep|pause|wait)");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @ForceExtract
    public static void run() {
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Unable to put non-script thread to sleep.");
        try {
            synchronized (thread.lock) {
                thread.lock.wait();
            }
        } catch (InterruptedException ex) {
            throw new ScriptRuntimeError("Sleep effect interrupted.", ex);
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("wait") && !thing.equals("sleep") && !thing.equals("pause")) return null;
        return super.match(thing, context);
    }
}
