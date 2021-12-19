package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.Member;

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
