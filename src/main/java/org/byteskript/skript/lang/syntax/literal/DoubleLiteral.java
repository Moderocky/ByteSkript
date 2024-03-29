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
    name = "Double Literal",
    description = """
        A decimal `0.43` value.
        Use the suffix `D` to ensure this is a double.
        """,
    examples = {
        """
            set {var} to 0.5
            set {var} to 89D
                """
    }
)
public class DoubleLiteral extends Literal<Double> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^-?\\d+(?:\\.\\d+)?[Dd]?(?![\\d.#FfLl])");
    private static final int LOW = 48, HIGH = 57;
    
    public DoubleLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "double literal");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String string = match.matcher().group();
        assert string.length() > 0;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Double value = parse(match.matcher().group());
        method.writeCode(WriteInstruction.loadConstant(value));
        try {
            method.writeCode(WriteInstruction.invokeStatic(Double.class.getMethod("valueOf", double.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Double parse(String input) {
        if (input.toUpperCase(Locale.ROOT).endsWith("D")) return Double.valueOf(input.substring(0, input.length() - 1));
        return Double.valueOf(input);
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
    
    @Override
    public Type getReturnType() {
        return CommonTypes.DOUBLE;
    }
}
