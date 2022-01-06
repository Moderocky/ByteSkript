/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

public abstract class SimpleEntry extends Element {
    public SimpleEntry(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    @Override
    public CompileState getSubState() {
        return CompileState.MEMBER_BODY;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.MEMBER_BODY;
    }
}
