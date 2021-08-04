package mx.kenzie.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

public class BooleanLiteral extends SimpleExpression {
    
    public BooleanLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "true", "false");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final boolean value = match.matcher().group().equals("true");
        method.writeCode(WriteInstruction.push(value));
        method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("true") && !thing.equals("false")) return null;
        return super.match(thing, context);
    }
    
}
