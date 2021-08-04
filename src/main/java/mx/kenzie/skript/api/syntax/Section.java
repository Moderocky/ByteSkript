package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.Context;

public abstract class Section extends Element {
    public Section(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    public abstract void onSectionExit(Context context);
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
