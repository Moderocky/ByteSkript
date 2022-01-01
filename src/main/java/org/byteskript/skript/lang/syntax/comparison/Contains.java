/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.lang.reflect.Method;

@Documentation(
    name = "Contains",
    description = """
        Check whether the first object contains the second.
        Applies to strings, lists and other collection types.""",
    examples = {
        "assert \"hello\" contains \"h\"",
        """
            if {list} contains 3:
                print "hello"
                """
    }
)
public class Contains extends RelationalExpression {
    
    public Contains() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% contains %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" contains ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = OperatorHandler.class.getDeclaredMethod("contains", Object.class, Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            context.setState(CompileState.STATEMENT);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
}
