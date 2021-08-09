package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class EventValueExpression extends Element implements SyntaxElement {
    
    public EventValueExpression(final Library provider, final String... patterns) {
        super(provider, StandardElements.EXPRESSION, correct(patterns));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.GET);
        assert target != null;
        assert target.getReturnType() != void.class;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
    protected static String[] correct(final String... strings) {
        final List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add("%Event%-" + string);
        }
        return list.toArray(new String[0]);
    }
    
}
