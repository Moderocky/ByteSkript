package mx.kenzie.skript.compiler;

import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.syntax.EventValueExpression;

public class AutomaticValueExpression extends EventValueExpression {
    
    public AutomaticValueExpression(Library provider, String pattern) {
        super(provider, pattern);
    }
    
}
