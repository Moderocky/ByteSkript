/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

public class TypeExpression extends SimpleExpression {
    
    public TypeExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "type");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.CLASS;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.CLASS.equals(type) || CommonTypes.TYPE.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
    public Type getType(String string, Context context) {
        for (final Map.Entry<String, Type> entry : context.getTypeMap().entrySet()) {
            if (!entry.getKey().toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) continue;
            return entry.getValue();
        }
        if (string.contains("/")) return new Type(string);
        return null;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        final Type type = this.getType(thing, context);
        if (type == null) return null;
        final Matcher matcher = Pattern.fakeMatcher(thing);
        return new Pattern.Match(matcher, type, new Type[0]);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadClassConstant(((Type) match.meta())));
    }
    
}
