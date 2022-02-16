/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

import java.lang.reflect.Method;

public class ConverterExpression extends SimpleExpression {
    
    public ConverterExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% [parsed] as a[n] %Type%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" as a")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final Type type = context.getCompileCurrent().nested()[1].match().meta();
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = ExtractedSyntaxCalls.class.getMethod("convert", Object.class, Object.class);
        this.writeCall(method, target, context);
        method.writeCode(WriteInstruction.cast(type));
    }
}
