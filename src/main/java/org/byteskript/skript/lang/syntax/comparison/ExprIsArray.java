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
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

@Documentation(
    name = "Is Array",
    description = "Check whether an object is an array.",
    examples = {
        "assert (1, 2) is an array",
        """
            if {var} is an array:
                print "hello"
                """
    }
)
public class ExprIsArray extends RelationalExpression {
    
    public ExprIsArray() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is|are) a[n]  array");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("isArray", Object.class));
            handlers.put(StandardHandlers.GET, OperatorHandler.class.getMethod("isArray", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // todo something is wrong with this syntax
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(" array")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
}
