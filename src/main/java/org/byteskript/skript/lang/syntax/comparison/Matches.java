/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.lang.reflect.Method;

public class Matches extends RelationalExpression {
    
    public Matches() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%String% matches %Pattern%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" matches ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = OperatorHandler.class.getDeclaredMethod("matches", Object.class, Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            context.setState(CompileState.STATEMENT);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
    @Override
    public String description() {
        return """
            Check whether the first string matches the given RegEx pattern.""";
    }
    
}
