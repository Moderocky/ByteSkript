package org.byteskript.skript.lang.syntax.map;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

import java.lang.reflect.Method;

public class KeyInMap extends RelationalExpression implements Referent {
    
    public KeyInMap() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[key ]%Object% in [map ]%Map%");
        try {
            handlers.put(StandardHandlers.GET, ExtractedSyntaxCalls.class.getMethod("getMapValue", Object.class, Object.class));
            handlers.put(StandardHandlers.SET, ExtractedSyntaxCalls.class.getMethod("setMapValue", Object.class, Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, ExtractedSyntaxCalls.class.getMethod("deleteMapValue", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" in ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.MAP;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || CommonTypes.REFERENT.equals(type);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    
}
