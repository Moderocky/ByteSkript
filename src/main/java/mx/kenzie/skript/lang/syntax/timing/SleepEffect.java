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
