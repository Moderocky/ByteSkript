package org.byteskript.skript.lang.syntax.timing;

import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.threading.ScriptThread;

public class SleepEffect extends Effect {
    
    public SleepEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(sleep|pause|wait)");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("wait") && !thing.equals("sleep") && !thing.equals("pause")) return null;
        return super.match(thing, context);
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
}
