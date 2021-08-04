package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.Property;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.element.StandardElements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class PropertyExpression extends SimpleExpression implements Property, Referent {
    
    protected final Type type;
    protected final String property;
    protected final java.util.regex.Pattern first;
    protected final java.util.regex.Pattern second;
    protected final Map<Type, Handlers> handlersMap = new HashMap<>();
    
    public PropertyExpression(Library library, Type valueType, String property) {
        super(library, StandardElements.EXPRESSION, property + " of %Object%", "%Object%'s " + property);
        this.type = valueType;
        this.property = property;
        this.first = java.util.regex.Pattern.compile("[the ]" + property + " of (?<input>.+)");
        this.second = java.util.regex.Pattern.compile("(?<input>.+)'s " + property);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    private static record Meta(boolean type, String name, String input) {
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" of ") || !thing.endsWith("'s ")) return null;
        if (thing.length() < 5) return null;
        return super.match(thing, context);
    }
    
    @Override
    public String name() {
        return property;
    }
    
    @Override
    public Type getPropertyType() {
        return type;
    }
    
    @Override
    public Method getHandler(HandlerType type, Type target) {
        return handlersMap.getOrDefault(target, Handlers.EMPTY).get(type);
    }
    
    @Override
    public void addHandler(HandlerType type, Type target, Method handle) {
        handlersMap.putIfAbsent(target, new Handlers());
        handlersMap.get(target).put(type, handle);
    }
    
}
