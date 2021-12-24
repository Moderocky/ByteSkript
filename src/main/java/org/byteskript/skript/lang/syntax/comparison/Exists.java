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

import java.lang.reflect.Method;
import java.util.Objects;

public class Exists extends RelationalExpression {
    
    public Exists() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (exists|is set)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(" exists")
            && !thing.contains(" is set")
        ) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = Objects.class.getDeclaredMethod("isNull", Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            method.writeCode((writer, visitor) -> {
                // Much faster method of inverting the boolean
                visitor.visitInsn(4);
                visitor.visitInsn(130);
            });
            method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
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
            Check an object exists (isn't 'null')""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert \"hello\" exists",
            """
                if {var} exists:
                    print {var}"""
        };
    }
    
}
