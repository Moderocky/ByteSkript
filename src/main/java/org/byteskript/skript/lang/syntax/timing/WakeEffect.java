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

public class WakeEffect extends Effect {
    
    public WakeEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wake[ up] %Thread%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("wake ")) return null;
        return super.match(thing, context);
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
}
