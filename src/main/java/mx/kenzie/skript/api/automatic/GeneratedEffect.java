package mx.kenzie.skript.api.automatic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.element.StandardElements;

import java.lang.reflect.Method;

public final class GeneratedEffect extends Effect {
    
    private final Method target;
    
    public GeneratedEffect(Library provider, final Method target, String... patterns) {
        super(provider, StandardElements.EFFECT, patterns);
        this.target = target;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        this.writeCall(method, target, context);
        if (target.getReturnType() != void.class) method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
}
