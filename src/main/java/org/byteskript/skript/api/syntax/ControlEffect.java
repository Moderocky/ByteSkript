/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

public abstract class ControlEffect extends Effect {
    public ControlEffect(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    public abstract HandlerType getType(Context context, Pattern.Match match);
    
}
