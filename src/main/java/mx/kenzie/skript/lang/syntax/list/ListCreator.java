package mx.kenzie.skript.lang.syntax.list;

import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.type.DataList;

public class ListCreator extends SimpleExpression {
    
    public ListCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a ]new list");
        try {
            handlers.put(StandardHandlers.GET, this.getClass().getMethod("create"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.LIST;
    }
    
    @ForceExtract
    public static DataList create() {
        return new DataList();
    }
    
}
