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

public class IntegerLiteral extends Literal<Integer> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\\d+(?![\\d.#LFD])");
    private static final int LOW = 48, HIGH = 57;
    
    public IntegerLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "int literal");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.INTEGER.equals(type) || CommonTypes.NUMBER.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String string = match.matcher().group();
        assert string.length() > 0;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Integer value = Integer.valueOf(match.matcher().group());
        method.writeCode(WriteInstruction.loadConstant(value));
        try {
            method.writeCode(WriteInstruction.invokeStatic(Integer.class.getMethod("valueOf", int.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Integer parse(String input) {
        return Integer.valueOf(input);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        final char c = thing.charAt(0);
        if (c < LOW || c > HIGH) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find()) return new Pattern.Match(matcher);
        return null;
    }
}
