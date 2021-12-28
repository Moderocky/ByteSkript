/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ExternalFunctionExpression extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")\\((?<params>.*)\\) from (?<location>.+)");
    
    public ExternalFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function(...) from %Source%");
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
        final Type location = new Type(details.location);
//        method.writeCode(WriteInstruction.invokeStatic(location, CommonTypes.OBJECT, details.name, parameters));
        
        final Function function = new Function(details.name, location, CommonTypes.OBJECT, details.arguments);
        method.writeCode(function.invoke(context.getType().internalName()));
    }
    
    private record FunctionDetails(String name, Type[] arguments, String location) {
    }
    
    private final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^" + SkriptLangSpec.IDENTIFIER.pattern() + "(?:/" + SkriptLangSpec.IDENTIFIER.pattern() + ")*$");
    
    private Pattern.Match createMatch(String thing, Context context) {
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String name = matcher.group("name");
        final String params = matcher.group("params");
        final String location = matcher.group("location");
        if (!pattern.matcher(location).matches()) return null;
        if (location.contains("\"")) return null;
        final Type[] parameters = getParams(params);
        final Matcher dummy = java.util.regex.Pattern.compile(buildDummyPattern(name, parameters.length, location))
            .matcher(thing);
        dummy.find();
        final List<Type> types = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            types.add(CommonTypes.OBJECT);
        }
        return new Pattern.Match(dummy, new FunctionDetails(name, parameters, location), types.toArray(new Type[0]));
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
    
    private Type[] getParams(String params) {
        if (params.isBlank()) return new Type[0];
        int nest = 0;
        final List<Type> types = new ArrayList<>();
        int count = 1;
        boolean atomic = false;
        for (char c : params.toCharArray()) {
            if (c == '(') nest++;
            else if (c == ')') nest--;
            else if (c == '@' && nest < 1) atomic = true;
            else if (c == ',' && nest < 1) {
                count++;
                if (atomic) types.add(CommonTypes.ATOMIC);
                else types.add(CommonTypes.OBJECT);
                atomic = false;
            }
        }
        if (atomic) types.add(CommonTypes.ATOMIC);
        else types.add(CommonTypes.OBJECT);
        return types.toArray(new Type[0]);
    }
    
}
