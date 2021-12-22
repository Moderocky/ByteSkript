package mx.kenzie.skript.lang.syntax.variable;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.GlobalVariableMap;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

public class GlobalVariableExpression extends VariableExpression implements Referent {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\{(?<name>!" + SkriptLangSpec.IDENTIFIER + ")\\}");
    
    public GlobalVariableExpression() {
        super();
        try {
            handlers.put(StandardHandlers.GET, GlobalVariableMap.class.getMethod("getVariable", Object.class));
            handlers.put(StandardHandlers.FIND, GlobalVariableMap.class.getMethod("getVariable", Object.class));
            handlers.put(StandardHandlers.SET, GlobalVariableMap.class.getMethod("setVariable", Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, GlobalVariableMap.class.getMethod("deleteVariable", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 4) return null;
        if (thing.charAt(1) != '!') return null;
        if (thing.charAt(0) != '{') return null;
        if (!thing.endsWith("}")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        return new Pattern.Match(matcher);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = match.matcher().group("name");
        method.writeCode(WriteInstruction.loadConstant(name));
        if (context.getHandlerMode().expectInputs())
            method.writeCode(WriteInstruction.swap()); // name, arg
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
