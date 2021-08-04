package mx.kenzie.skript.api.syntax;

import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;

public abstract class ControlEffect extends Effect {
    public ControlEffect(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    public abstract HandlerType getType(Context context, Pattern.Match match);
    
}
