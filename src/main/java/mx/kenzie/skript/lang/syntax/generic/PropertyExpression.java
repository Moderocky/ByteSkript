package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

public class PropertyExpression extends RelationalExpression {
    protected final java.util.regex.Pattern[] patterns;
    
    public PropertyExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "<property> of %Object%");
        this.patterns = new java.util.regex.Pattern[]{
            java.util.regex.Pattern.compile("^(?:the )?(?<name>" + SkriptLangSpec.IDENTIFIER + ") of (?<input>.+)$"),
            java.util.regex.Pattern.compile("^(?<input>.+)'s (?<name>" + SkriptLangSpec.IDENTIFIER + ")$"),
            java.util.regex.Pattern.compile("^(?<input>" + SkriptLangSpec.IDENTIFIER + ")-(?<name>" + SkriptLangSpec.IDENTIFIER + ")$")
            // third pattern only permitted for literals
        };
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!(thing.contains(" of ") || thing.contains("'s ") || thing.contains("-"))) return null;
        for (int i = 0; i < patterns.length; i++) {
            final java.util.regex.Pattern pattern = patterns[i];
            final Matcher matcher = pattern.matcher(thing);
            if (!matcher.find()) continue;
            final String name = matcher.group("name");
            final Matcher dummy = createDummy(thing, i, matcher);
            dummy.find();
            return new Pattern.Match(dummy, name, CommonTypes.OBJECT);
        }
        return null;
    }
    
    private Matcher createDummy(String thing, int index, Matcher matcher) {
        final StringBuilder builder = new StringBuilder();
        switch (index) {
            case 0 -> {
                if (thing.startsWith("the ")) builder.append("the ");
                builder.append(matcher.group("name")).append(" of (.+)");
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            case 1 -> {
                builder.append("(.+)'s ");
                builder.append(matcher.group("name"));
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            case 2 -> {
                builder.append("(.+)-");
                builder.append(matcher.group("name"));
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            default -> throw new IllegalStateException();
        }
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final String name = (String) match.meta();
        final HandlerType type = context.getHandlerMode();
        final MethodErasure target = context.useHandle(name, type);
        context.getMethod().writeCode(WriteInstruction.invokeStatic(context.getType(), target));
    }
}
