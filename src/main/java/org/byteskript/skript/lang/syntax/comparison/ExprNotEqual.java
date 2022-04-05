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

import java.lang.reflect.Method;
import java.util.Objects;

@Documentation(
    name = "Not Equal",
    description = """
        Check whether two objects are not equal in value.""",
    examples = {
        """
            assert "hello" is not 6
            assert 6 != 5
            """
    }
)
public class ExprNotEqual extends RelationalExpression {
    
    public ExprNotEqual() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION,
            "%Object% (isn't|is not|aren't|are not) %Object%",
            "%Object% ?(≠|!=) ?%Object%"
        );
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" is")
            && !thing.contains(" are")
            && !thing.contains("≠")
            && !thing.contains("!=")
        ) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = Objects.class.getDeclaredMethod("equals", Object.class, Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            method.writeCode((writer, visitor) -> {
                // Much faster method of inverting the boolean
                visitor.visitInsn(4);
                visitor.visitInsn(130);
            });
            method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
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
