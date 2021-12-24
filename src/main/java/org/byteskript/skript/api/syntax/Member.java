package org.byteskript.skript.api.syntax;

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
        context.setMethod(null);
        context.setField(null);
        context.setState(CompileState.ROOT);
    }
    
}
