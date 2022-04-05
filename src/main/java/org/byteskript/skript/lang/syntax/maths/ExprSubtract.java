/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.maths;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

@Documentation(
    name = "Subtraction",
    description = """
        Subtract two numbers.
        """,
    examples = {
        """
            set {var} to 5 - 2
                """
    }
)
public class ExprSubtract extends RelationalExpression {
    
    public ExprSubtract() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% ?- ?%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("subtract", Object.class, Object.class));
            handlers.put(StandardHandlers.GET, OperatorHandler.class.getMethod("subtract", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("-")) return null;
        return super.match(thing, context);
    }
    
}
