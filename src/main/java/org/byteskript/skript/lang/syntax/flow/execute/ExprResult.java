/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.execute;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprRunnableSection;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

import java.lang.reflect.Method;

@Documentation(
    name = "Result Of",
    description = """
        Retrieves the value of an executable, such as a supplier or a dynamic function.""",
    examples = {
        """
            set {var} to the result of {supplier}
            set {var} to the result of a new supplier:
                return "hello"
            assert {var} is "hello"
                    """
    }
)
public class ExprResult extends SimpleExpression {
    
    public ExprResult() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] result of %Executable%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("result of ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        final MethodBuilder method = context.getMethod();
        assert method != null;
        if (tree.current() instanceof ExprRunnableSection) {
            final Method target = Runnable.class.getMethod("run");
            method.writeCode(WriteInstruction.invokeInterface(target));
            context.setState(CompileState.CODE_BODY);
            return;
        }
        final Method target = ExtractedSyntaxCalls.class.getMethod("run", Object.class);
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
}
