package mx.kenzie.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.Function;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

public class NoArgsFunctionExpression extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(SkriptLangSpec.IDENTIFIER.pattern() + "\\(\\)");
    
    public NoArgsFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function()");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith("()")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String pattern = matcher.group();
        return new Pattern.Match(matcher, pattern.substring(0, pattern.length() - 2));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = (String) match.meta();
        final Function function = context.getDefaultFunction(name);
        method.writeCode(function.invoke(0));
    }
    
}
