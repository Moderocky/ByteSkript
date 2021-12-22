package mx.kenzie.skript.lang.syntax.list;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.ExtractedSyntaxCalls;

public class IndexOfList extends RelationalExpression implements Referent {
    
    public IndexOfList() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "index %Number% in [list ]%List%");
        try {
            handlers.put(StandardHandlers.GET, ExtractedSyntaxCalls.class.getMethod("getListValue", Object.class, Object.class));
            handlers.put(StandardHandlers.FIND, ExtractedSyntaxCalls.class.getMethod("getListValue", Object.class, Object.class));
            handlers.put(StandardHandlers.SET, ExtractedSyntaxCalls.class.getMethod("setListValue", Object.class, Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, ExtractedSyntaxCalls.class.getMethod("deleteListValue", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("index ")) return null;
        if (!thing.contains(" in ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.LIST;
    }
    
    
}
