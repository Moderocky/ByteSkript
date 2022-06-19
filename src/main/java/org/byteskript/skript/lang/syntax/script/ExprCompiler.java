/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.script;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;
import org.byteskript.skript.runtime.internal.ModifiableCompiler;

@Documentation(
    name = "Compiler",
    description = """
        Obtains the current compiler. This may not exist.
        This can be used to check whether this installation is able to load scripts.
        If the compiler is `null`, the load effect will be unavailable.
        """,
    examples = {
        """
            set {var} to the compiler
            if the compiler exists:
                print "can load scripts :)"
                """
    }
)
public class ExprCompiler extends SimpleExpression {
    
    public ExprCompiler() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] compiler");
        handlers.put(StandardHandlers.GET, findMethod(ExtractedSyntaxCalls.class, "getCompiler"));
    }
    
    @Override
    public Type getReturnType() {
        return new Type(ModifiableCompiler.class);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || super.allowAsInputFor(type);
    }
    
}
