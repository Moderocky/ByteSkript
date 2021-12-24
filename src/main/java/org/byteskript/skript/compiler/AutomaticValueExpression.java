package org.byteskript.skript.compiler;

import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.EventValueExpression;

public class AutomaticValueExpression extends EventValueExpression {
    
    public AutomaticValueExpression(Library provider, String pattern) {
        super(provider, pattern);
    }
    
}
