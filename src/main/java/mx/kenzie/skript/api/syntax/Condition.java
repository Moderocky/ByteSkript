package mx.kenzie.skript.api.syntax;

import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.lang.handler.StandardHandlers;

public abstract class Condition extends Element implements SyntaxElement {
    
    public Condition(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
        try {
            handlers.put(StandardHandlers.FIND, this.getClass().getMethod("find", Object[].class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    public abstract Object find(Object... delta);
    
}
