/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

public class GTEQ extends RelationalExpression {
    
    public GTEQ() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is greater than or equal to|>=) ?%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("gteq", Object.class, Object.class));
            handlers.put(StandardHandlers.GET, OperatorHandler.class.getMethod("gteq", Object.class, Object.class));
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
            Check whether the first number is greater than or equal to the second.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert 4 is greater than or equal to 3",
            """
                if {var} >= 6:
                    print "hello"
                    """
        };
    }
    
}
