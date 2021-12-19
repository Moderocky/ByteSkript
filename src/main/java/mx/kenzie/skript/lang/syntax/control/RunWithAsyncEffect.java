package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.Member;

import java.lang.reflect.Method;

public class RunWithAsyncEffect extends ControlEffect {
    
    public RunWithAsyncEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable% with %Object% (async[hronously]|in [the ]background)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        if (!thing.contains(" with ")) return null;
        if (!thing.contains(" async") && !thing.contains("background")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = Member.class.getMethod("runAsync", Object.class, Object.class);
        this.writeCall(method, target, context);
        method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
