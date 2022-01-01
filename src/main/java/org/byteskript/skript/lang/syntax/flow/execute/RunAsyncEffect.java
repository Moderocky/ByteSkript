/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.execute;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.Member;

import java.lang.reflect.Method;

@Documentation(
    name = "Run in Background",
    description = """
        Runs the given executable in the background.
        Background processes ignore waits from the current process.
        """,
    examples = {
        """
            run {function} in the background
            run {runnable} in the background
                    """
    }
)
public class RunAsyncEffect extends ControlEffect {
    
    public RunAsyncEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable% (async[hronously]|in [the] background)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        if (!thing.contains(" async") && !thing.contains(" background")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        // todo functions
        final Method target = Member.class.getMethod("runAsync", Object.class);
        this.writeCall(method, target, context);
        if (target.getReturnType() != void.class) method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
