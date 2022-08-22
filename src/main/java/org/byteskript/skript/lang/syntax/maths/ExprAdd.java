/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.maths;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

@Documentation(
    name = "Addition",
    description = """
        Add two objects together.
        This supports joining strings and numbers.
        """,
    examples = {
        """
            set {var} to 0.5 + 15 // 15.5
            set {var} to "hello" + 5 // "hello5"
            set {var} to "1" + 1 // "11"
            set {var} to "hello " + "there" // "hello there"
                """
    }
)
public class ExprAdd extends SymbolJoiner {
    
    public ExprAdd() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% ?\\\\+ ?%Object%");
        try {
            handlers.put(StandardHandlers.FIND, OperatorHandler.class.getMethod("add", Object.class, Object.class));
            handlers.put(StandardHandlers.GET, OperatorHandler.class.getMethod("add", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("+")) return null;
        return super.match(thing, context);
    }
    
    @Override
    char joiner() {
        return '+';
    }
    
}
