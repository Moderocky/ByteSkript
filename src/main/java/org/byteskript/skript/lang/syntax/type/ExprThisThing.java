/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "This Object",
    description = """
        Returns the current object, while in a function from a type.
        Outside a custom type this will not be usable.
        """,
    examples = """
        type Square:
            function my_func:
                trigger:
                    return this object
        """
)
public class ExprThisThing extends SimpleExpression {
    
    public ExprThisThing() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "this (thing|object)");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadThis());
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("this ")) return null;
        if (!context.hasFlag(AreaFlag.IN_TYPE)) return null;
        return super.match(thing, context);
    }
    
}
