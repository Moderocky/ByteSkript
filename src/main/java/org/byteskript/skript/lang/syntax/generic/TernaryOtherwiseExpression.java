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
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
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
public class TernaryOtherwiseExpression extends SimpleExpression {
    
    public TernaryOtherwiseExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "if %Boolean% then %Object% otherwise %Object%",
            "%Boolean% \\\\? %Object% : %Object%");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.OBJECT);
    }
    
    // switched to raw bytecode form
    // had to use the avatar state for this manipulation :o
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Label first = new Label(), second = new Label();
        method.writeCode((writer, visitor) -> {
            visitor.visitInsn(93); // dup2x1
            visitor.visitInsn(87); // pop
            visitor.visitInsn(87); // pop
            visitor.visitTypeInsn(192, "java/lang/Boolean"); // checkcast
            visitor.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false); // virtual
            visitor.visitJumpInsn(153, first); // if 0
            visitor.visitInsn(87); // pop
            visitor.visitJumpInsn(167, second); // goto
            visitor.visitLabel(first);
            visitor.visitInsn(95); // swap
            visitor.visitInsn(87); // pop
            visitor.visitLabel(second); // x or y
        });
    }
    
}
