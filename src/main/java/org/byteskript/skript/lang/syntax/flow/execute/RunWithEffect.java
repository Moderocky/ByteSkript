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
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.Member;

import java.lang.reflect.Method;
import java.util.Collection;

public class RunWithEffect extends ControlEffect {
    
    public RunWithEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable% with %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        if (!thing.contains(" with ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = RunWithEffect.class.getMethod("run", Object.class, Object.class);
        this.writeCall(method, target, context);
        method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @ForceExtract
    public static Object run(Object thing, Object args)
        throws Throwable {
        final Object[] arguments;
        if (args instanceof Collection<?> collection) arguments = collection.toArray();
        else if (args instanceof Object[] array) arguments = array;
        else arguments = new Object[]{args};
        if (thing instanceof Method method)
            return method.invoke(null, arguments);
        else if (thing instanceof MethodAccessor<?> runnable)
            runnable.invoke(arguments);
        else if (thing instanceof Member runnable)
            runnable.invoke(arguments);
        return null;
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
