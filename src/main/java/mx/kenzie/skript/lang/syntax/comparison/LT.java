package mx.kenzie.skript.lang.syntax.comparison;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.OperatorHandler;

public class LT extends RelationalExpression {
    
    public LT() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is less than|<) ?%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("lt", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
    @Override
    public String description() {
        return """
            Check whether the first number is less than the second.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert 4 is less than 5",
            """
                if {var} < 6:
                    print "hello"
                    """
        };
    }
    
}
