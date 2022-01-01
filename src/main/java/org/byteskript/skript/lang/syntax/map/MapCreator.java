/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.map;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceInline;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.type.DataMap;

@Documentation(
    name = "New Map",
    description = """
        A new key/value map.
        """,
    examples = {
        """
            set {map} to a new map
            set "blob" in {map} to 55.3
                """
    }
)
public class MapCreator extends SimpleExpression {
    
    public MapCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new map");
        handlers.put(StandardHandlers.GET, findMethod(this.getClass(), "create"));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.MAP;
    }
    
    @ForceInline
    public static DataMap create() {
        return new DataMap();
    }
    
}
