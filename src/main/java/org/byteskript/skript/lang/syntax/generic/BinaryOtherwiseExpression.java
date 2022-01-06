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
    name = "Otherwise (Binary)",
    description = """
        Returns a default value if the first input is null.
        """,
    examples = {
        """
            set {var} to {number} ? 0
                """
    }
)
public class BinaryOtherwiseExpression extends SimpleExpression {
    
    public BinaryOtherwiseExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (otherwise|\\\\?) %Object%");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final Label first = new Label(), second = new Label();
        method.writeCode((writer, visitor) -> {
            visitor.visitInsn(95); // swap
            visitor.visitInsn(89); // dup
            visitor.visitJumpInsn(199, first); // notnull
            visitor.visitInsn(87); // pop
            visitor.visitJumpInsn(167, second); // goto
            visitor.visitLabel(first);
            visitor.visitInsn(95); // swap
            visitor.visitInsn(87); // pop
            visitor.visitLabel(second); // x or y
        });
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.OBJECT);
    }
    
}
