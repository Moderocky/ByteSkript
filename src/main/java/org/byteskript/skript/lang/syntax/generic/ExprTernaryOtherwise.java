/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "Otherwise (Ternary)",
    description = """
        If the condition is true, returns the second input.
        Otherwise, returns the third.
        """,
    examples = {
        """
            set {var} to true ? {number} : 0
            set {var} to (if {thing} is true then 56 otherwise 31)
                """
    }
)
public class ExprTernaryOtherwise extends SimpleExpression {
    
    public ExprTernaryOtherwise() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "if %Boolean% then %Object% otherwise %Object%",
            "%Boolean% \\\\? %Object% : %Object%");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final ElementTree[] nested = context.getCompileCurrent().nested();
        nested[1].compile = false;
        nested[2].compile = false;
    }
    
    // switched to raw bytecode form
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Label alternative = new Label(), end = new Label();
        final ElementTree[] nested = context.getCompileCurrent().nested();
        method.writeCode((writer, visitor) -> {
            visitor.visitTypeInsn(192, "java/lang/Boolean"); // checkcast
            visitor.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false); // virtual
            visitor.visitJumpInsn(153, alternative); // if 0
        });
        nested[1].compile = true;
        nested[1].preCompile(context);
        nested[1].compile(context);
        method.writeCode((writer, visitor) -> {
            visitor.visitJumpInsn(167, end); // goto
            visitor.visitLabel(alternative);
        });
        nested[2].compile = true;
        nested[2].preCompile(context);
        nested[2].compile(context);
        method.writeCode((writer, visitor) -> {
            visitor.visitLabel(end); // x or y
        });
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.OBJECT);
    }
    
}
