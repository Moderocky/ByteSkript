/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Boolean Literal",
    description = """
        A `true` or `false` value.
        """,
    examples = {
        """
            set {var} to true
            if {var} is true
                """
    }
)
public class BooleanLiteral extends Literal<Boolean> {
    
    public BooleanLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "true", "false");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final boolean value = match.matcher().group().equals("true");
        method.writeCode(WriteInstruction.push(value));
        method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
    }
    
    @Override
    public Boolean parse(String input) {
        return input.equals("true");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("true") && !thing.equals("false")) return null;
        return super.match(thing, context);
    }
    
}
