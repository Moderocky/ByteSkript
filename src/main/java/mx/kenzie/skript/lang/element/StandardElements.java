package mx.kenzie.skript.lang.element;

import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.SkriptLangSpec;

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
