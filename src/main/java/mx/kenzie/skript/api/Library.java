package mx.kenzie.skript.api;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.compiler.Context;

import java.util.Collection;

public interface Library {
    
    String name();
    
    Collection<SyntaxElement> getHandlers(final State state, final LanguageElement expected, final Context context);
    
    Collection<PropertyHandler> getProperties();
    
    SyntaxElement[] getSyntax();
    
    LanguageElement[] getConstructs();
    
    Type[] getTypes();
    
    /**
     * Runtime dependencies to be included in complete archives.
     */
    Collection<PostCompileClass> getRuntime();
    
}
