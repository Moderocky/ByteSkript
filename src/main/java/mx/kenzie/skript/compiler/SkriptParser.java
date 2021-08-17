package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.Type;

public interface SkriptParser {
    
    ElementTree parseLine(final String line, final FileContext context);
    
    ElementTree assembleStatement(final String statement, final FileContext context);
    
    ElementTree assembleExpression(String expression, final Type expected, final FileContext context);
    
}
