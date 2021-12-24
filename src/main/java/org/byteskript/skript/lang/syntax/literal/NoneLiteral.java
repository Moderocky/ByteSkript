/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.literal;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

public class NoneLiteral extends Literal<Void> {
    
    public NoneLiteral() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "null", "none");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.pushNull());
    }
    
    @Override
    public Void parse(String input) {
        return null;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("null") && !thing.equals("none")) return null;
        return super.match(thing, context);
    }
    
}
