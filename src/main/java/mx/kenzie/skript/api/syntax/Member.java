package mx.kenzie.skript.api.syntax;

import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;

public abstract class Member extends Section {
    public Member(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public LanguageElement getType() {
        return super.getType();
    }
    
    @Override
    public void onSectionExit(Context context) {
        context.emptyVariables();
        context.setMethod(null);
        context.setField(null);
        context.setState(CompileState.ROOT);
    }
    
}
