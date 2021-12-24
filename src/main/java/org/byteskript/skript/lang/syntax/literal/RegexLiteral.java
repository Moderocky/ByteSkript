/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

public class RegexLiteral extends Literal<java.util.regex.Pattern> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\\/[^\\/\\\\\\r\\n]*(?:\\\\.[^\\/\\\\\\r\\n]*)*\\/\n");
    
    public RegexLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "string literal");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final String string = match.matcher().group();
        assert string.length() > 1;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String literal = string.substring(1, string.length() - 1);
        method.writeCode(WriteInstruction.loadConstant(literal));
        final Method target = java.util.regex.Pattern.class.getMethod("compile", String.class);
        this.writeCall(method, target, context);
    }
    
    @Override
    public java.util.regex.Pattern parse(String input) {
        return java.util.regex.Pattern.compile(input.substring(1, input.length() - 1));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.charAt(0) != '/') return null;
        if (thing.charAt(thing.length() - 1) != '/') return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find()) return new Pattern.Match(matcher);
        return null;
    }
}
