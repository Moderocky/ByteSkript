/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.list;

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
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

@Documentation(
    name = "Index of List",
    description = """
        Accesses the given index of a collection.
        This can be used to get, set or delete its value.
        Indices start at *zero*.
        """,
    examples = {
        """
            set {var} index 0 of {list}
            set {var} index 1 of {array}
                """
    }
)
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
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.LIST;
    }
    
    
}
