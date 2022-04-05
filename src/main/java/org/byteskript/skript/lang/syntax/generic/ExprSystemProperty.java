/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

@Documentation(
    name = "System Property",
    description = """
        Used to set/get/delete a system property by name.
        """,
    examples = {
        """
            set {var} to {number} ? 0
                """
    }
)
public class ExprSystemProperty extends SimpleExpression implements Referent {
    
    public ExprSystemProperty() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] system property %String%");
        try {
            handlers.put(StandardHandlers.GET, ExprSystemProperty.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.FIND, ExprSystemProperty.class.getMethod("getProperty", String.class));
            handlers.put(StandardHandlers.SET, ExprSystemProperty.class.getMethod("setProperty", String.class, String.class));
            handlers.put(StandardHandlers.DELETE, ExprSystemProperty.class.getMethod("clearProperty", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("system property ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.REFERENT);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.STRING;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.OBJECT;
    }
    
}
