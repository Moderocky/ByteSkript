package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;

/**
 * A special variety of expression designed as a placeholder to modify or contain an inner expression
 * and not to be present in the output tree.
 * <p>
 * Designed for brackets, etc.
 */
public abstract class InnerModifyExpression extends Element implements SyntaxElement {
    
    public InnerModifyExpression(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public final void compile(Context context, Pattern.Match match) {
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
}
