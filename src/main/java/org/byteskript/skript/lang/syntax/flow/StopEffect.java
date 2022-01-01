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
    name = "Stop",
    description = """
        Stops the current trigger.
        If the caller was expecting a value, it will receive `null`.
        """,
    examples = {
        """
            function test:
                trigger:
                    print "hello"
                    stop
                    print "there" // not reached
                """
    }
)
public class StopEffect extends Effect {
    
    public StopEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(stop|return)");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.pushNull());
        method.writeCode(WriteInstruction.returnObject());
        context.setState(CompileState.CODE_BODY);
    }
    
}
