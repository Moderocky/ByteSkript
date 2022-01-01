/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

@Documentation(
    name = "New Line",
    description = """
        Returns the system-local line separator character(s).
        This may be different from the typical `\\n`.
        """,
    examples = {
        """
            print "hello" + newline + "there"
                """
    }
)
public class NewLineExpression extends SimpleExpression {
    
    public NewLineExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "(new[ ]line|nl)");
        handlers.put(StandardHandlers.GET, findMethod(System.class, "lineSeparator"));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.STRING.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
}
