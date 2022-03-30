/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.Locale;
import java.util.regex.Matcher;

@Documentation(
    name = "Long Literal",
    description = """
        A very large whole number `55L` value.
        Use the suffix `L` to ensure this is a long.
        """,
    examples = {
        """
            set {var} to 50000000000L
            set {var} to -300L
                """
    }
)
public class LongLiteral extends Literal<Long> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^-?\\d+[Ll](?![\\d.#DFdf])");
    private static final int LOW = 48, HIGH = 57;
    
    public LongLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "long literal");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String string = match.matcher().group();
        assert string.length() > 0;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Long value = parse(match.matcher().group());
        method.writeCode(WriteInstruction.loadConstant(value));
        try {
            method.writeCode(WriteInstruction.invokeStatic(Long.class.getMethod("valueOf", long.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Long parse(String input) {
        if (input.toUpperCase(Locale.ROOT).endsWith("L")) return Long.valueOf(input.substring(0, input.length() - 1));
        return Long.valueOf(input);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        final char c = thing.charAt(0);
        if (c != '-' && (c < LOW || c > HIGH)) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find()) return new Pattern.Match(matcher, thing);
        return null;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.NUMBER.equals(type) || CommonTypes.OBJECT.equals(type);
    }
}
