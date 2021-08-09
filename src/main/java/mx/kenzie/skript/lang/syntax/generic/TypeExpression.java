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

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

public class TypeExpression extends SimpleExpression {
    
    public TypeExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "type");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.CLASS;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.CLASS.equals(type) || CommonTypes.TYPE.equals(type);
    }
    
    public Type getType(String string, Context context) {
        for (Map.Entry<String, Type> entry : context.getTypeMap().entrySet()) {
            if (!entry.getKey().toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) continue;
            return entry.getValue();
        }
        return null;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        for (Map.Entry<String, Type> entry : context.getTypeMap().entrySet()) {
            if (!entry.getKey().toLowerCase(Locale.ROOT).equals(thing.toLowerCase(Locale.ROOT))) continue;
            final Matcher matcher = java.util.regex.Pattern.compile(thing).matcher(thing);
            matcher.find();
            return new Pattern.Match(matcher, entry.getValue(), new Type[0]);
        }
        return null;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadClassConstant(((Type) match.meta())));
    }
    
}
