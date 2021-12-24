package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public abstract class Literal<Type> extends Element implements SyntaxElement {
    
    public Literal(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.GET);
        assert target != null;
        assert target.getReturnType() != void.class;
        this.writeCall(method, target, context);
    }
    
    public abstract Type parse(String input);
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return
            (state == CompileState.STATEMENT || state == CompileState.ENTRY_VALUE)
                && context.hasCurrentUnit();
    }
    
}
