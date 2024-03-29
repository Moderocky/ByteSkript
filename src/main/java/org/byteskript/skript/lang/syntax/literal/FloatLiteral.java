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
    name = "Float Literal",
    description = """
        A less-precise decimal `0.43F` value.
        """,
    examples = {
        """
            set {var} to 0.5F
            set {var} to 89F
                """
    }
)
public class FloatLiteral extends Literal<Float> {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^-?\\d+(?:\\.\\d+)?[Ff](?![\\d.#DLdl])");
    private static final int LOW = 48, HIGH = 57;
    
    public FloatLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "float literal");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String string = match.matcher().group();
        assert string.length() > 0;
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Float value = parse(match.matcher().group());
        method.writeCode(WriteInstruction.loadConstant(value));
        try {
            method.writeCode(WriteInstruction.invokeStatic(Float.class.getMethod("valueOf", float.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Float parse(String input) {
        if (input.toUpperCase(Locale.ROOT).endsWith("F")) return Float.valueOf(input.substring(0, input.length() - 1));
        return Float.valueOf(input);
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
        return CommonTypes.FLOAT;
    }
}
