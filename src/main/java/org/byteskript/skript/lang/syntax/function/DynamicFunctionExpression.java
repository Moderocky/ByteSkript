/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.Member;

@Documentation(
    name = "Dynamic Function Handle",
    description = """
        Finds the (runnable) handle for this function.
        This can be used to store functions or run them in the background.
        Once a handle is found it can be re-used.
        """,
    examples = {
        """
            set {func} to the function "my_func(a, b) from skript/myscript"
            run {func} with (1, 2) in the background
                """
    }
)
public class DynamicFunctionExpression extends SimpleExpression {
    
    public DynamicFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION,
            "[the] function %String%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("function ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        for (final ElementTree tree : context.getCompileCurrent().nested()) {
            tree.takeAtomic = true;
        }
        context.getMethod().writeCode(WriteInstruction.loadClassConstant(context.getBuilder().getType()));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.invokeStatic(Member.class.getMethod("getFunction", Object.class, String.class)));
    }
    
}
