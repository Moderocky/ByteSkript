package mx.kenzie.skript.lang.syntax.code.map;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.PropertyExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.type.DataList;

import java.util.Map;

public class KeysOfMap extends PropertyExpression {
    
    public KeysOfMap() {
        super(SkriptLangSpec.LIBRARY, CommonTypes.LIST, "keys");
        try {
            handlers.put(StandardHandlers.GET, KeysOfMap.class.getMethod("get", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.MAP;
    }
    
    public static DataList get(Object target) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        return new DataList(map.keySet());
    }
    
}
