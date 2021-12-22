package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.internal.ExtractedSyntaxCalls;

import java.lang.reflect.Method;

public class SystemInputExpression extends SimpleExpression {
    
    public SystemInputExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ](system|console) input");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith("input")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean requiresMainThread() {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = ExtractedSyntaxCalls.class.getMethod("getSystemInput");
        this.writeCall(method, target, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
