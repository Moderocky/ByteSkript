package mx.kenzie.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.TriggerHolder;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.data.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class FunctionMember extends TriggerHolder {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("function (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ") ?\\((?<params>" + SkriptLangSpec.IDENTIFIER.pattern() + "(?:, " + SkriptLangSpec.IDENTIFIER.pattern() + ")*)\\)");
    
    public FunctionMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "function(...)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 10) return null;
        if (!thing.startsWith("function ")) return null;
        if (!thing.contains("(") || !thing.contains(")")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find() && matcher.group("name") != null && matcher.group("params") != null)
            return new Pattern.Match(matcher);
        return null;
    }
    
    @Override
    public void onSectionExit(Context context) {
        context.registerFunction(new mx.kenzie.skript.compiler.structure.Function(context.getMethod().getErasure()
            .name(), context.getType()));
        super.onSectionExit(context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        super.compile(context, match);
        final MethodBuilder method = context.getMethod();
        method
            .addAnnotation(Function.class).setVisible(true)
            .addValue("name", method.getErasure().name())
            .addValue("arguments", method.getErasure().parameterTypes().length)
            .addValue("async", false);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public Type returnType(Context context, Pattern.Match match) {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Type[] parameters(Context context, Pattern.Match match) {
        final String string = match.matcher().group("params");
        final String[] strings = string.split(",");
        final List<Type> types = new ArrayList<>();
        for (String s : strings) {
            context.getVariable(s.trim()).parameter = true;
            types.add(CommonTypes.OBJECT);
        }
        return types.toArray(new Type[0]);
    }
    
    @Override
    public String callSiteName(Context context, Pattern.Match match) {
        return match.matcher().group("name");
    }
}
