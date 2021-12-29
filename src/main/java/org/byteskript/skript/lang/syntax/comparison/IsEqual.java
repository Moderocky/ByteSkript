/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.lang.reflect.Method;

public class IsEqual extends RelationalExpression {
    
    public IsEqual() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is|is equal to|are|=|==) %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" is ")
            && !thing.contains(" =")
            && !thing.contains(" are ")
        ) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        final Method target = findMethod(OperatorHandler.class, "equals", Object.class, Object.class);
        method.writeCode(WriteInstruction.invokeStatic(target));
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert 1 is 1",
            """
                if {var} = 6:
                    print "hello"
                    """
        };
    }
    
}
