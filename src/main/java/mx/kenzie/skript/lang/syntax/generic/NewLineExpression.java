package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

public class NewLineExpression extends SimpleExpression {
    
    public NewLineExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "(new[ ]line|nl)");
        handlers.put(StandardHandlers.GET, findMethod(System.class, "lineSeparator"));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.STRING.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
}
