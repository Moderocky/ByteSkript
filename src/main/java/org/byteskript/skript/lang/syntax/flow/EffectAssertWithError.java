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
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.lang.reflect.Method;

@Documentation(
    name = "Assert",
    description = """
        Tests that the given value is true, halting the program if it isn't.
        Prints the error message provided.
        """,
    examples = {
        """
            assert {var} is true: "Var wasn't true"
            assert 1 is 1: "1 wasn't 1"
                """
    }
)
public class EffectAssertWithError extends Effect {
    
    public EffectAssertWithError() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "assert %Boolean%: %String%");
        handlers.put(StandardHandlers.RUN, findMethod(OperatorHandler.class, "assertion", Object.class, Object.class, Class.class, int.class));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Method target = handlers.get(StandardHandlers.RUN);
        final int line = context.lineNumber();
        method.writeCode(WriteInstruction.loadClassConstant(method.finish().getType()));
        method.writeCode(WriteInstruction.push(line));
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("assert ")) return null;
        if (!thing.contains(": ")) return null;
        return super.match(thing, context);
    }
    
}
