package org.byteskript.skript.lang.element;

import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.SkriptLangSpec;

public enum StandardElements implements LanguageElement {
    METADATA,
    NODE,
    SECTION,
    MEMBER,
    EFFECT,
    CONDITION,
    EXPRESSION,
    
    ;
    
    @Override
    public Library getProvider() {
        return SkriptLangSpec.LIBRARY;
    }
}
