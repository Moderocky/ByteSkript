package org.byteskript.skript.lang.syntax.maths;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

public class MultiplyExpression extends RelationalExpression {
    
    public MultiplyExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Number% ?(\\\\*|×) ?%Number%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("multiply", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.NUMBER;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("*") && !thing.contains("×")) return null;
        return super.match(thing, context);
    }
    
}
