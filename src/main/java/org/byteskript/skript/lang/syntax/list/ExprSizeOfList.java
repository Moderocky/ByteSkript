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
    name = "Size of Collection",
    description = """
        Returns the size of a collection.
        Indices of the collection start at *zero*.
        """,
    examples = {
        """
            print size of {list}
                """
    }
)
public class ExprSizeOfList extends RelationalExpression implements Referent {
    
    public ExprSizeOfList() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "size of [list] %Object%");
        try {
            handlers.put(StandardHandlers.GET, ExtractedSyntaxCalls.class.getMethod("getListSize", Object.class));
            handlers.put(StandardHandlers.FIND, ExtractedSyntaxCalls.class.getMethod("getListSize", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("size of ")) return null;
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
