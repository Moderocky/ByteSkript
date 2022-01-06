/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.list;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceInline;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.type.DataList;

@Documentation(
    name = "New List",
    description = """
        Creates a new modifiable list.
        Values can be added to this.
        """,
    examples = {
        """
            set {list} to a new list
            add "hello" to {list}
            loop {word} in {list}:
                print {word}
                """
    }
)
public class ListCreator extends SimpleExpression {
    
    public ListCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new list");
        try {
            handlers.put(StandardHandlers.GET, this.getClass().getMethod("create"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @ForceInline
    public static DataList create() {
        return new DataList();
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.LIST;
    }
    
}
