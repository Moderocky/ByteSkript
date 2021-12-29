/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

import java.lang.reflect.Method;

public class SystemInputExpression extends SimpleExpression {
    
    public SystemInputExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] (system|console) input");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(" input")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean requiresMainThread() {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = ExtractedSyntaxCalls.class.getMethod("getSystemInput");
        this.writeCall(method, target, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
}
