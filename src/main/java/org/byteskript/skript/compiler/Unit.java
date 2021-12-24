/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

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
