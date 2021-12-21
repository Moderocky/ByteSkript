package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class SystemPropertyExpression extends SimpleExpression implements Referent {
    
    public SystemPropertyExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ]system property %String%");
        try {
            handlers.put(StandardHandlers.GET, SystemPropertyExpression.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.FIND, SystemPropertyExpression.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.SET, SystemPropertyExpression.class.getMethod("setProperty", String.class, String.class));
            handlers.put(StandardHandlers.DELETE, SystemPropertyExpression.class.getMethod("clearProperty", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.REFERENT);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.OBJECT;
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
    public static String getProperty(String name) {
        return System.getProperty(name);
    }
    
    @ForceExtract
    public static String setProperty(String name, String value) {
        return System.setProperty(name, value);
    }
    
    @ForceExtract
    public static String clearProperty(String name) {
        return System.clearProperty(name);
    }
    
}
