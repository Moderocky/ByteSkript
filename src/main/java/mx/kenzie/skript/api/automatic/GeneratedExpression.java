package mx.kenzie.skript.api.automatic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.element.StandardElements;

import java.lang.reflect.Method;

public final class GeneratedExpression extends SimpleExpression {
    
    private final Method target;
    private final Type value;
    
    public GeneratedExpression(Library provider, final Method target, String... patterns) {
        super(provider, StandardElements.EXPRESSION, patterns);
        this.target = target;
        this.value = new Type(target.getReturnType());
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert target.getReturnType() != void.class;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return value;
    }
    
}
