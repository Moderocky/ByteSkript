/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.compiler.structure.ErrorDetails;

public interface SkriptParser {
    
    ElementTree parseLine(final String line, final FileContext context);
    
    ElementTree assembleStatement(final String statement, final FileContext context, final ErrorDetails details);
    
    ElementTree assembleExpression(String expression, final Type expected, final FileContext context, final ErrorDetails details);
    
}
