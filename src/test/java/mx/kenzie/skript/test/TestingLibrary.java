package mx.kenzie.skript.test;

import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.ModifiableLibrary;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

public class TestingLibrary extends ModifiableLibrary {
    public TestingLibrary() {
        super("testing");
        this.registerSyntax(CompileState.CODE_BODY, new ThrowException(this));
    }
    
    public static class ThrowException extends Effect {
    
        public ThrowException(Library provider) {
            super(provider, StandardElements.EFFECT, "throw exception");
            handlers.put(StandardHandlers.RUN, findMethod(ThrowException.class, "error"));
        }
        
        public static void error() {
            throw new RuntimeException("Testing error.");
        }
    }
    
}
