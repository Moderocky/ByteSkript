package mx.kenzie.skript.lang.syntax.flow;

import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptAssertionError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

public class AssertEffect extends Effect {
    
    public AssertEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "assert %Boolean%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("assertion", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("assert ")) return null;
        return super.match(thing, context);
    }
    
    @ForceExtract
    public static void assertion(Object object) {
        if (object == null)
            throw new ScriptAssertionError();
        else if (object instanceof Boolean boo && !boo)
            throw new ScriptAssertionError();
        else if (object instanceof Number number && number.intValue() == 0)
            throw new ScriptAssertionError();
    }
    
}
