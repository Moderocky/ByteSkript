package mx.kenzie.skript.lang.syntax.list;

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

import java.util.List;

public class IndexOfList extends RelationalExpression implements Referent {
    
    public IndexOfList() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "index %Number% in [list ]%List%");
        try {
            handlers.put(StandardHandlers.GET, IndexOfList.class.getMethod("get", Object.class, Object.class));
            handlers.put(StandardHandlers.SET, IndexOfList.class.getMethod("set", Object.class, Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, IndexOfList.class.getMethod("delete", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("index ")) return null;
        if (!thing.contains(" in ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.LIST;
    }
    
    public static Object get(Object key, Object target) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        return list.get(number.intValue());
    }
    
    @SuppressWarnings("unchecked")
    public static void set(Object key, Object target, Object value) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        list.remove(number.intValue());
        list.add(number.intValue(), value);
    }
    
    public static void delete(Object key, Object target) {
        if (!(key instanceof Number number))
            throw new ScriptRuntimeError("The given index must be a number.");
        if (!(target instanceof List list))
            throw new ScriptRuntimeError("The given collection must be a map.");
        list.remove(number.intValue());
    }
    
    
}
