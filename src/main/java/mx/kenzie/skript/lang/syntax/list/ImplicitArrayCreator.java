package mx.kenzie.skript.lang.syntax.list;

import mx.kenzie.foundation.*;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class ImplicitArrayCreator extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?:(?:a )?new array of )?\\((?<params>.+)\\)");
    
    public ImplicitArrayCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "new array of (...)");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final int expected = context.getCompileCurrent().nested().length;
        final ClassBuilder builder = context.getBuilder();
        final Type[] parameters = new Type[expected];
        Arrays.fill(parameters, CommonTypes.OBJECT);
        final MethodErasure erasure = new MethodErasure(CommonTypes.OBJECTS, "lambda$packArray", parameters);
        if (!builder.hasMatching(erasure)) {
            final MethodBuilder target = builder.addMatching(erasure)
                .setModifiers(0x00000002 | 0x00000008 | 0x00001000);
            target.writeCode(WriteInstruction.newArray(Object.class, parameters.length));
            for (int i = 0; i < parameters.length; i++) {
                target.writeCode(WriteInstruction.duplicate());
                target.writeCode(WriteInstruction.loadObject(i));
                target.writeCode(WriteInstruction.arrayStoreObject(i));
            }
            target.writeCode(WriteInstruction.returnObject());
        }
        method.writeCode(WriteInstruction.invokeStatic(builder.getType(), erasure));
    }
    
    //region Matcher
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.startsWith("(") && !thing.contains(",")) return null;
        if (!thing.startsWith("(")
            && !thing.startsWith("a new array of (")
            && !thing.startsWith("new array of (")
        ) return null;
        if (!thing.endsWith(")")) return null;
        return createMatch(thing, context);
    }
    
    private Pattern.Match createMatch(String thing, Context context) {
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String params = matcher.group("params");
        final int count = getParams(params);
        final Matcher dummy = java.util.regex.Pattern.compile(buildDummyPattern(count, thing)).matcher(thing);
        dummy.find();
        final List<Type> types = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            types.add(CommonTypes.OBJECT);
        }
        return new Pattern.Match(dummy, null, types.toArray(new Type[0]));
    }
    
    private String buildDummyPattern(int params, String thing) {
        final StringBuilder builder = new StringBuilder();
        if (!thing.startsWith("("))
            builder.append(thing, 0, thing.indexOf('('));
        builder.append("\\(");
        for (int i = 0; i < params; i++) {
            if (i > 0) builder.append(", ");
            builder.append("(.+)");
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
    //endregion
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECTS;
    }
    
}
