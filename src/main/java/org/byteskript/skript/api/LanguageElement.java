/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;
import org.byteskript.skript.lang.element.StandardElements;

/**
 * Language elements. For advanced use only.
 * See {@link StandardElements} for the built-in ones.
 */
@Description("""
    Language elements. For advanced use only.
    This is designed for adding entirely new pieces of grammar to the language.
    
    See StandardElements for the built-in ones that most syntax must use.
    """)
public interface LanguageElement {
    
    String name();
    
    Library getProvider();
    
}
