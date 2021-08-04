package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public abstract class Effect extends Element implements SyntaxElement {
    
    public Effect(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.RUN);
        assert target != null;
        this.writeCall(method, target, context);
        if (target.getReturnType() != void.class) method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.CODE_BODY && context.hasCurrentUnit();
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
