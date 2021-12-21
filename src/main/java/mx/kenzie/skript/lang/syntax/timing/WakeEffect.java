package mx.kenzie.skript.lang.syntax.timing;

import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.threading.ScriptThread;

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
