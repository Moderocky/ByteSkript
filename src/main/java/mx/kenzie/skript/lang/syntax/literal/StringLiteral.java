package mx.kenzie.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.Literal;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

public class StringLiteral extends Literal<String> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\"[^\"\\\\\\r\\n]*(?:\\\\.[^\"\\\\\\r\\n]*)*\"");
    
    public StringLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "string literal");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
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
        return input.substring(1, input.length() - 1);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.charAt(0) != '"') return null;
        if (thing.charAt(thing.length() - 1) != '"') return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find()) return new Pattern.Match(matcher);
        return null;
    }
}
