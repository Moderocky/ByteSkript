package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

public class CurrentScriptExpression extends SimpleExpression {
    
    public CurrentScriptExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ][current ]script");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EVENT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadConstant(method.finish().getName()));
    }
    
}
