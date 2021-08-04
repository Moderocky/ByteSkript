package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.InnerModifyExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

public class BracketExpression extends InnerModifyExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\((.+)\\)");
    
    public BracketExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "(brackets)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("(")) return null;
        if (!thing.endsWith(")")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        return new Pattern.Match(matcher, CommonTypes.OBJECT);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
}
