/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

@Documentation(
    name = "Less Than",
    description = "Check whether the first number is less than the second.",
    examples = {
        "assert 4 is less than 5",
        """
            if {var} < 6:
                print "hello"
                """
    }
)
public class ExprLT extends RelationalExpression {
    
    public ExprLT() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object%( is less than | ?< ?)%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("lt", Object.class, Object.class));
            handlers.put(StandardHandlers.GET, OperatorHandler.class.getMethod("lt", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
}
