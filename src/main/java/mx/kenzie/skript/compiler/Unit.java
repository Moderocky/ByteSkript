package mx.kenzie.skript.compiler;

import mx.kenzie.skript.api.LanguageElement;

public class Unit {
    
    final LanguageElement type;
    
    public Unit(LanguageElement type) {
        this.type = type;
    }
    
    public LanguageElement getType() {
        return type;
    }
    
    
}
