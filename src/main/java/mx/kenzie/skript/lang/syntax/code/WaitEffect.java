package mx.kenzie.skript.lang.syntax.code;

import mx.kenzie.skript.api.note.ForceInline;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.time.Duration;

public class WaitEffect extends Effect {
    
    public WaitEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wait[ for] %Duration%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("wait ")) return null;
        return super.match(thing, context);
    }
    
    @ForceInline
    public static void run(Object object) {
        if (!(object instanceof Duration duration))
            throw new ScriptRuntimeError("Wait effect requires duration.");
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            throw new ScriptRuntimeError("Wait effect interrupted.", ex);
        }
    }
}
