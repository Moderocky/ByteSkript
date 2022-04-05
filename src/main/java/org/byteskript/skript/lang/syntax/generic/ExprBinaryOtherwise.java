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
public class ExprBinaryOtherwise extends SimpleExpression {
    
    public ExprBinaryOtherwise() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (otherwise|\\\\?) %Object%");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        context.getCompileCurrent().nested()[1].compile = false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        final ElementTree tree = context.getCompileCurrent().nested()[1];
        final Label end = new Label();
        method.writeCode((writer, visitor) -> {
            visitor.visitInsn(89); // dup
            visitor.visitJumpInsn(199, end); // notnull
            visitor.visitInsn(87); // pop other null
        });
        tree.compile = true;
        tree.preCompile(context);
        tree.compile(context);
        method.writeCode((writer, visitor) -> {
            visitor.visitLabel(end); // x or y
        });
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.OBJECT);
    }
    
}
