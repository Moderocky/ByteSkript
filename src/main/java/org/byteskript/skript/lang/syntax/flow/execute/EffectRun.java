/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.execute;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.mirror.MethodAccessor;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprRunnableSection;
import org.byteskript.skript.runtime.internal.Member;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.Future;

@Documentation(
    name = "Run",
    description = """
        Runs the given executable (function, lambda, etc.)
        The current block will wait for this to finish.
        """,
    examples = {
        """
            run my_func()
            run {runnable}
                    """
    }
)
public class EffectRun extends ControlEffect {
    
    public EffectRun() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable%");
    }
    
    @ForceExtract
    public static Object run(Object thing)
        throws Throwable {
        if (thing instanceof Method method)
            return method.invoke(null);
        else if (thing instanceof MethodAccessor<?> runnable)
            runnable.invoke();
        else if (thing instanceof Member runnable)
            runnable.invoke();
        else if (thing instanceof Runnable runnable)
            runnable.run();
        else if (thing instanceof Future future)
            return future.get();
        else if (thing instanceof Duration duration)
            Thread.sleep(duration.toMillis());
        return null;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        final MethodBuilder method = context.getMethod();
        assert method != null;
        if (tree.current() instanceof ExprRunnableSection) {
            final Method target = Runnable.class.getMethod("run");
            method.writeCode(WriteInstruction.invokeInterface(target));
            context.setState(CompileState.CODE_BODY);
            return;
        } else {
            final Method target = EffectRun.class.getMethod("run", Object.class);
            this.writeCall(method, target, context);
        }
        method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
