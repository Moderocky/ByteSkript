package mx.kenzie.skript.lang.syntax.code.list;

import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.util.Collection;

public class ClearList extends Effect {
    
    public ClearList() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "clear %List%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("clear ")) return null;
        return super.match(thing, context);
    }
    
    public static void run(Object object) {
        if (object instanceof Collection list) list.clear();
    }
    
    
}
