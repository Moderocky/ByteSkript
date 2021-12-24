package org.byteskript.skript.api;

import org.byteskript.skript.lang.element.StandardElements;

/**
 * Language elements. For advanced use only.
 * See {@link StandardElements} for the built-in ones.
 */
public interface LanguageElement {
    
    String name();
    
    Library getProvider();
    
}
