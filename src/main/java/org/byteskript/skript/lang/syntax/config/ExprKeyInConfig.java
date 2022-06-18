/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.config;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.config.ConfigMap;

import java.lang.reflect.Method;

@Documentation(
    name = "Key in Map",
    description = """
        Accesses this key in the provided map.
        This can use the set/get/delete handlers.
        """,
    examples = {
        """
            set {var} to "hello" in map {map}
            set ("name" in map {map}) to "Bob"
                """
    }
)
public class ExprKeyInConfig extends RelationalExpression implements Referent {
    
    public ExprKeyInConfig() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%String% from [config] %Config%");
        try {
            handlers.put(StandardHandlers.GET, ConfigMap.class.getMethod("getMapValue", Object.class, Object.class));
            handlers.put(StandardHandlers.SET, ConfigMap.class.getMethod("setMapValue", Object.class, Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, ConfigMap.class.getMethod("deleteMapValue", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" from ")) return null;
        return super.match(thing, context);
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
        return CommonTypes.CONFIG;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || CommonTypes.REFERENT.equals(type);
    }

    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }


}
