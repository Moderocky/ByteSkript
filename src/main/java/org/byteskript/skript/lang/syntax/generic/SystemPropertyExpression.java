/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class SystemPropertyExpression extends SimpleExpression implements Referent {
    
    public SystemPropertyExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ]system property %String%");
        try {
            handlers.put(StandardHandlers.GET, SystemPropertyExpression.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.FIND, SystemPropertyExpression.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.SET, SystemPropertyExpression.class.getMethod("setProperty", String.class, String.class));
            handlers.put(StandardHandlers.DELETE, SystemPropertyExpression.class.getMethod("clearProperty", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.REFERENT);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    @ForceExtract
    public static String getProperty(String name) {
        return System.getProperty(name);
    }
    
    @ForceExtract
    public static String setProperty(String name, String value) {
        return System.setProperty(name, value);
    }
    
    @ForceExtract
    public static String clearProperty(String name) {
        return System.clearProperty(name);
    }
    
}
