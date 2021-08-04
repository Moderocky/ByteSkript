package mx.kenzie.skript.lang.syntax.map;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.util.Map;

public class KeyInMap extends RelationalExpression implements Referent {
    
    public KeyInMap() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[key ]%Object% in [map ]%Map%");
        try {
            handlers.put(StandardHandlers.GET, KeyInMap.class.getMethod("get", Object.class, Object.class));
            handlers.put(StandardHandlers.SET, KeyInMap.class.getMethod("set", Object.class, Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, KeyInMap.class.getMethod("delete", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" in ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.MAP;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || CommonTypes.REFERENT.equals(type);
    }
    
    public static Object get(Object key, Object target) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        return map.get(key);
    }
    
    public static void set(Object key, Object target, Object value) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        map.put(key, value);
    }
    
    public static void delete(Object key, Object target) {
        if (!(target instanceof Map map))
            throw new ScriptRuntimeError("The given collection must be a map.");
        map.remove(key);
    }
    
    
}
