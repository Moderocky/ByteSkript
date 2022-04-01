/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.structure.SectionMeta;

public abstract class Member extends Section {
    public Member(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public LanguageElement getType() {
        return super.getType();
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.emptyVariables();
        context.setMethod(null, true);
        context.setField(null);
        context.setState(CompileState.ROOT);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
