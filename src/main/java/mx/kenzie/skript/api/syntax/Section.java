package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.error.ScriptCompileError;

public abstract class Section extends Element {
    public Section(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    public abstract void onSectionExit(Context context);
    
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'" + name() + "' must be used as a section-header.");
    }
    
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        // Rarely used
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
