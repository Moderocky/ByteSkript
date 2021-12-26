/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

/**
 * This handles all properties and delegates the compilation
 * to the expression stubs.
 */
public class PropertyExpression extends RelationalExpression implements Referent {
    protected final java.util.regex.Pattern[] patterns;
    
    public PropertyExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "<property> of %Object%");
        this.patterns = new java.util.regex.Pattern[]{
            java.util.regex.Pattern.compile("^(?:the )?(?<name>" + SkriptLangSpec.IDENTIFIER + ") of (?<input>.+)$"),
            java.util.regex.Pattern.compile("^(?<input>.+)'s (?<name>" + SkriptLangSpec.IDENTIFIER + ")$"),
            java.util.regex.Pattern.compile("^(?<input>" + SkriptLangSpec.IDENTIFIER + ")-(?<name>" + SkriptLangSpec.IDENTIFIER + ")$")
            // third pattern only permitted for literals
        };
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        final int i;
        if (thing.contains(" of ")) i = 0;
        else if (thing.contains("'s ")) i = 1;
        else if (thing.contains("-")) i = 2;
        else return null;
        final java.util.regex.Pattern pattern = patterns[i];
        final Matcher matcher = pattern.matcher(thing);
        if (!matcher.find()) return null;
        final String name = matcher.group("name");
        if (!context.hasHandle(name, context.getHandlerMode())) return null;
        final Matcher dummy = createDummy(thing, i, matcher);
        dummy.find();
        return new Pattern.Match(dummy, name, CommonTypes.OBJECT);
    }
    
    private Matcher createDummy(String thing, int index, Matcher matcher) {
        final StringBuilder builder = new StringBuilder();
        switch (index) {
            case 0 -> {
                if (thing.startsWith("the ")) builder.append("the ");
                builder.append(matcher.group("name")).append(" of (.+)");
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            case 1 -> {
                builder.append("(.+)'s ");
                builder.append(matcher.group("name"));
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            case 2 -> {
                builder.append("(.+)-");
                builder.append(matcher.group("name"));
                return java.util.regex.Pattern.compile(builder.toString()).matcher(thing);
            }
            default -> throw new IllegalStateException();
        }
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final String name = (String) match.meta();
        final HandlerType type = context.getHandlerMode();
        final MethodErasure target = context.useHandle(name, type);
        context.getMethod().writeCode(WriteInstruction.invokeStatic(context.getType(), target));
    }
}
