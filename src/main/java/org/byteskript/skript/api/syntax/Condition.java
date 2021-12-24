/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.lang.handler.StandardHandlers;

public abstract class Condition extends Element implements SyntaxElement {
    
    public Condition(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
        try {
            handlers.put(StandardHandlers.FIND, this.getClass().getMethod("find", Object[].class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    public abstract Object find(Object... delta);
    
}
