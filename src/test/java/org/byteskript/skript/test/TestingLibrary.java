package org.byteskript.skript.test;

import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

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
