/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "String Literal",
    description = """
        A text `"hello"` value.
        Anything can go inside this single-line text.
        Quotes `"` and comments `// /*` need to be escaped with a `\\` character.
        """,
    examples = {
        """
            set {var} to "hello there!"
            set {var} to "how\\nare you?"
                """
    }
)
public class StringLiteral extends Literal<String> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\"[^\"\\\\\\r\\n]*(?:\\\\.[^\"\\\\\\r\\n]*)*\"");
    
    public StringLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "string literal");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String string = match.matcher().group();
        assert string.length() > 1;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String literal = string.substring(1, string.length() - 1);
        method.writeCode(WriteInstruction.loadConstant(literal));
    }
    
    @Override
    public String parse(String input) {
        if (input.indexOf('\\') < 0) return input.substring(1, input.length() - 1);
        final StringBuilder builder = new StringBuilder();
        boolean escape = false;
        for (final char c : input.substring(1, input.length() - 1).toCharArray()) {
            if (c == '\\') escape = true;
            else if (escape) {
                escape = false;
                switch (c) {
                    case 'n':
                        builder.append('\n');
                    case 'r':
                        builder.append('\r');
                    case 'b':
                        builder.append('\b');
                    case 'f':
                        builder.append('\f');
                    case 't':
                        builder.append('\t');
                    default:
                        builder.append(c);
                }
            } else builder.append(c);
        }
        return builder.toString();
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.charAt(0) != '"') return null;
        if (thing.charAt(thing.length() - 1) != '"') return null;
        if (!matches(thing.substring(1, thing.length() - 1))) return null;
        return new Pattern.Match(Pattern.fakeMatcher(thing));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    private boolean matches(final String string) {
        boolean escape = false;
        for (final char c : string.toCharArray()) {
            if (escape) {
                escape = false;
                continue;
            }
            switch (c) {
                case '"':
                    return false;
                case '\\':
                    escape = true;
            }
        }
        return true;
    }
}
