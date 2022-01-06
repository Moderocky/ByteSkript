/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.timing;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Current Process",
    description = """
        Gets the current thread.
        Useful for managing delays and multi-process operations.
        """,
    examples = {
        """
            set {thread} to the current process
            run a new runnable in the background:
                wait 10 seconds
                wake {thread}
            sleep
            print "something woke me"
                """
    }
)
public class ThreadExpression extends SimpleExpression {
    
    public ThreadExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] [current] (process|thread)");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.THREAD;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.invokeStatic(Thread.class.getMethod("currentThread")));
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || CommonTypes.OBJECT.equals(type) || CommonTypes.THREAD.equals(type);
    }
    
}
