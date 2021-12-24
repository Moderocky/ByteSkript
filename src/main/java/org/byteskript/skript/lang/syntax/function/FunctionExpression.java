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

public class FunctionExpression extends SimpleExpression {
    
    static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")\\((?<params>.*)\\)");
    
    public FunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function(...)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(")")) return null;
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
        final Function function = context.getDefaultFunction(details.name);
        method.writeCode(function.invoke(details.arguments));
    }
    
    private record FunctionDetails(String name, int arguments) {
    }
    
    private Pattern.Match createMatch(String thing, Context context) {
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String name = matcher.group("name");
        final String params = matcher.group("params");
        final int count = getParams(params);
        final Matcher dummy = java.util.regex.Pattern.compile(buildDummyPattern(name, count)).matcher(thing);
        dummy.find();
        final List<Type> types = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            types.add(CommonTypes.OBJECT);
        }
        return new Pattern.Match(dummy, new FunctionDetails(name, count), types.toArray(new Type[0]));
    }
    
    private String buildDummyPattern(String name, int params) {
        final StringBuilder builder = new StringBuilder()
            .append(name).append("\\(");
        if (params > 0) {
            for (int i = 0; i < params; i++) {
                if (i > 0) builder.append(", ");
                builder.append("(.+)");
            }
        }
        return builder.append("\\)").toString();
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