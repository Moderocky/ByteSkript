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
    name = "Exit Thread",
    description = """
        Exits the current process (thread).
        This kills the process immediately, so no future instructions will be run.
        Other processes are not affected.
        """,
    examples = {
        """
            exit the current thread
                """
    }
)
public class EffectExitThread extends Effect {
    
    public EffectExitThread() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(exit|stop) %Thread%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("exit ") && !thing.startsWith("stop ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.invokeVirtual(Thread.class.getMethod("stop")));
        context.setState(CompileState.CODE_BODY);
    }
    
}
