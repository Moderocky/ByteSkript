package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;

public abstract class RelationalExpression extends ComplexExpression implements SyntaxElement {
    
    public RelationalExpression(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
}
