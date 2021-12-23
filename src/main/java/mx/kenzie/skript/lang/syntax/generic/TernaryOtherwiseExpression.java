package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class TernaryOtherwiseExpression extends SimpleExpression {
    
    public TernaryOtherwiseExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "if %Boolean% then %Object% otherwise %Object%",
            "%Boolean% \\\\? %Object% : %Object%");
        try {
            handlers.put(StandardHandlers.GET, TernaryOtherwiseExpression.class.getMethod("getResult", Boolean.class, Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.OBJECT);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    @ForceExtract
    public static Object getResult(Boolean check, Object first, Object def) {
        if (check == null) return def;
        if (check) return first;
        return def;
    }
    
}
