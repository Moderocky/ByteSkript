package mx.kenzie.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class ExternalFunctionExpression extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")\\((?<params>.*)\\) from (?<location>.+)");
    
    public ExternalFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function(...) from ...");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(") from ")) return null;
        if (!thing.contains("(")) return null;
        return createMatch(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        for (final ElementTree tree : context.getCompileCurrent().nested()) {
            tree.takeAtomic = true;
        }
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final FunctionDetails details = ((FunctionDetails) match.meta());
        assert details != null;
        final Type[] parameters = new Type[details.arguments];
        Arrays.fill(parameters, CommonTypes.OBJECT);
        final Type location = new Type(details.location);
        method.writeCode(WriteInstruction.invokeStatic(location, CommonTypes.OBJECT, details.name, parameters));
    }
    
    private record FunctionDetails(String name, int arguments, String location) {
    }
    
    private Pattern.Match createMatch(String thing, Context context) {
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String name = matcher.group("name");
        final String params = matcher.group("params");
        final String location = matcher.group("location");
        if (location.contains("\"")) return null;
        final int count = getParams(params);
        final Matcher dummy = java.util.regex.Pattern.compile(buildDummyPattern(name, count, location)).matcher(thing);
        dummy.find();
        final List<Type> types = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            types.add(CommonTypes.OBJECT);
        }
        return new Pattern.Match(dummy, new FunctionDetails(name, count, location), types.toArray(new Type[0]));
    }
    
    private String buildDummyPattern(String name, int params, String location) {
        final StringBuilder builder = new StringBuilder()
            .append(name).append("\\(");
        if (params > 0) {
            for (int i = 0; i < params; i++) {
                if (i > 0) builder.append(", ");
                builder.append("(.+)");
            }
        }
        return builder.append("\\) from ").append(location).toString();
    }
    
    private int getParams(String params) {
        if (params.isBlank()) return 0;
        int nest = 0;
        int count = 1;
        for (char c : params.toCharArray()) {
            if (c == '(') nest++;
            else if (c == ')') nest--;
            else if (c == ',' && nest < 1) count++;
        }
        return count;
    }
    
}
