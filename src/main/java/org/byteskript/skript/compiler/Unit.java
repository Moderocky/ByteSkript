package org.byteskript.skript.compiler;

import org.byteskript.skript.api.LanguageElement;

public class Unit {
    
    final LanguageElement type;
    
    public Unit(LanguageElement type) {
        this.type = type;
    }
    
    public LanguageElement getType() {
        return type;
    }
    
    
}
