/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Exit Program",
    description = """
        Exits the program. This ends all processes immediately.
        Some processes are capable of blocking this, in which case it will throw an error and kill the current process.
        """,
    examples = {
        """
            if true is false:
                exit the program
                """
    }
)
public class EffectExit extends Effect {
    
    public EffectExit() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "exit [the] program");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("exit ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.push0());
        method.writeCode(WriteInstruction.invokeStatic(System.class.getMethod("exit", int.class)));
        context.setState(CompileState.CODE_BODY);
    }
    
}
