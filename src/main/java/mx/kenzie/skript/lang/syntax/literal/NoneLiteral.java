package mx.kenzie.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.Literal;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

public class NoneLiteral extends Literal<Void> {
    
    public NoneLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "null", "none");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.pushNull());
    }
    
    @Override
    public Void parse(String input) {
        return null;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("null") && !thing.equals("none")) return null;
        return super.match(thing, context);
    }
    
}
