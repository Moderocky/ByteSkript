package mx.kenzie.skript.lang.syntax.code.map;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.type.DataMap;

public class MapCreator extends SimpleExpression {
    
    public MapCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a ]new map");
        try {
            handlers.put(StandardHandlers.GET, this.getClass().getMethod("create"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.MAP;
    }
    
    @ForceExtract
    public static DataMap create() {
        return new DataMap();
    }
    
}
