/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

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
