package org.byteskript.skript.lang.syntax.maths;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

public class SquareRootExpression extends RelationalExpression {
    
    public SquareRootExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ]sq[uare ]r[oo]t of %Number%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("root", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.NUMBER;
    }
    
}
