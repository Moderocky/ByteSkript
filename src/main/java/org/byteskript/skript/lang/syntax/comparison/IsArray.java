/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

public class IsArray extends RelationalExpression {
    
    public IsArray() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is|are) a[n] array");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(" array")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode((writer, visitor) -> {
            visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            visitor.visitMethodInsn(182, "java/lang/Class", "isArray", "()Z", false);
            visitor.visitMethodInsn(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        });
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
    @Override
    public String description() {
        return """
            Check whether an object is an array.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert (1, 2) is an array",
            """
                if {var} is an array:
                    print "hello"
                    """
        };
    }
    
}
