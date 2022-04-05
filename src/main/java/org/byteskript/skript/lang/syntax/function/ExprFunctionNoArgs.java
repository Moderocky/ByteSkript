/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

@Documentation(
    name = "Function (No Arguments)",
    description = """
        A function with no arguments.
        """,
    examples = {
        """
            run my_func()
                """
    }
)
public class ExprFunctionNoArgs extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(SkriptLangSpec.IDENTIFIER.pattern() + "\\(\\)");
    
    public ExprFunctionNoArgs() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function()");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith("()")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String pattern = matcher.group();
        return new Pattern.Match(matcher, pattern.substring(0, pattern.length() - 2));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = match.meta();
        final Function function = context.getDefaultFunction(name, 0);
        method.writeCode(function.invoke(context.getType().internalName()));
    }
    
}
