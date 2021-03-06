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
    name = "Matches",
    description = """
        Check whether the first string matches the given RegEx pattern.
        Check whether the first object matches the given query.""",
    examples = {
        """
            assert "hello" matches /.+/
            assert "blob" matches /blobs?/
            """
    }
)
public class ExprMatches extends RelationalExpression {
    
    public ExprMatches() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% matches %Query%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" matches ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = findMethod(OperatorHandler.class, "matches", Object.class, Object.class);
        method.writeCode(WriteInstruction.invokeStatic(target));
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
}
