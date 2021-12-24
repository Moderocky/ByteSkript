package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

public class LTEQ extends RelationalExpression {
    
    public LTEQ() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is less than or equal to|<=) ?%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("lteq", Object.class, Object.class));
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
            Check whether the first number is greater than the second.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert 4 is less than or equal to 4",
            """
                if {var} <= 6:
                    print "hello"
                    """
        };
    }
    
}
