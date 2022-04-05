/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Return",
    description = """
        Stops the current trigger, giving back this value to whatever started.
        This is designed for use in functions and suppliers.
        Some triggers cannot return a value, such as events.
        """,
    examples = {
        """
            return "hello"
            return 63
                """
    }
)
public class EffectReturn extends Effect {
    
    public EffectReturn() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "return %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("return ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Type type = method.getErasure().returnType();
        if (!type.equals(CommonTypes.OBJECT)) method.writeCode(WriteInstruction.cast(type));
        method.writeCode(WriteInstruction.returnObject());
        context.setState(CompileState.CODE_BODY);
    }
    
}
