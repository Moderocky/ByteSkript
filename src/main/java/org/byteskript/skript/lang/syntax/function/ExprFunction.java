/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Documentation(
    name = "Function (Local)",
    description = """
        Runs a function from this script or the 'skript' library.
        """,
    examples = {
        """
            set {var} to my_func(4)
            set {number} to sqrt(25)
                """
    }
)
public class ExprFunction extends SimpleExpression {
    
    static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")\\((?<params>.*)\\)");
    
    public ExprFunction() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function(...)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(")")) return null;
        if (!thing.contains("(")) return null;
        return createMatch(thing, context);
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
        for (int i = 0; i < count; i++) types.add(CommonTypes.OBJECT);
        return new Pattern.Match(dummy, new FunctionDetails(name, count), types.toArray(new Type[0]));
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
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree[] trees = context.getCompileCurrent().nested();
        if (context.getHandlerMode() == StandardHandlers.SET) {
            if (trees.length > 0) trees[0].compile = false;
        }
        for (final ElementTree tree : trees) {
            tree.takeAtomic = true;
        }
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final FunctionDetails details = match.meta();
        final Function function = context.getDefaultFunction(details.name, details.arguments);
        method.writeCode(function.invoke(context.getType().internalName()));
        if (!context.getHandlerMode().expectReturn()) method.writeCode(WriteInstruction.pop());
    }
    
    private record FunctionDetails(String name, int arguments) {
    }
    
}
