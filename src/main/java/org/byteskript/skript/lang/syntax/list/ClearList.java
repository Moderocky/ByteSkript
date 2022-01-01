/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.list;

import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Documentation(
    name = "Clear Collection",
    description = """
        Empties the given collection.
        """,
    examples = {
        """
            clear {list}
            clear {array}
            clear {map}
                """
    }
)
public class ClearList extends Effect {
    
    public ClearList() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "clear %Object%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "run", Object.class));
    }
    
    @ForceExtract
    public static void run(Object object) {
        if (object instanceof Collection list) list.clear();
        if (object instanceof Map map) map.clear(); // in case this is loaded in preference.
        else if (object instanceof Object[] array) Arrays.fill(array, null);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("clear ")) return null;
        return super.match(thing, context);
    }
    
    
}
