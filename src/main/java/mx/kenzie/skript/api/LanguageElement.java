package mx.kenzie.skript.api;

/**
 * Language elements. For advanced use only.
 * See {@link mx.kenzie.skript.lang.element.StandardElements} for the built-in ones.
 */
public interface LanguageElement {
    
    String name();
    
    Library getProvider();
    
}
