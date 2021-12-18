package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.PreVariable;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.OperatorHandler;

import java.util.regex.Matcher;

import static mx.kenzie.skript.lang.handler.StandardHandlers.SET;

public class VariableExpression extends SimpleExpression implements Referent {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\{(?<name>" + SkriptLangSpec.IDENTIFIER + ")\\}");
    
    public VariableExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "variable");
        try {
            handlers.put(StandardHandlers.ADD, OperatorHandler.class.getMethod("addObject", Object.class, Object.class));
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
        if (!thing.startsWith("{") || !thing.endsWith("}")) return null;
        if (thing.length() < 3) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        return new Pattern.Match(matcher);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.VOID;
    }

//    @Override
//    public void preCompile(Context context, Pattern.Match match) throws Throwable {
//        final MethodBuilder method = context.getMethod();
//        assert method != null;
//        super.preCompile(context, match);
//        final String name = match.matcher().group("name");
//        final PreVariable variable = context.getVariable(name);
//        final int slot = context.slotOf(variable);
//         if (context.getHandlerMode().equals(StandardHandlers.ADD)) {
//            method.writeCode(variable.load(slot));
//        } else if (context.getHandlerMode().equals(StandardHandlers.REMOVE)) {
//            method.writeCode(variable.load(slot));
//        }
//    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = match.matcher().group("name");
        final PreVariable variable = context.getVariable(name);
        final int slot = context.slotOf(variable);
        if (context.getHandlerMode().equals(SET)) {
            method.writeCode(variable.store(slot));
        } else if (context.getHandlerMode().equals(StandardHandlers.GET)) {
            method.writeCode(variable.load(slot));
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
