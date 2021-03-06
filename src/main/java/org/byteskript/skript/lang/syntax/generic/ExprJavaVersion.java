/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.Skript;

@Documentation(
    name = "Java Version",
    description = """
        Returns the class file version used to compile this script.
        This will be a number (61).
        """,
    examples = {
        """
            set {var} to the java version
                """
    }
)
public class ExprJavaVersion extends SimpleExpression {
    
    public ExprJavaVersion() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] java version");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.INTEGER;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadConstant(Skript.JAVA_VERSION));
        method.writeCode(WriteInstruction.invokeStatic(Integer.class.getMethod("valueOf", int.class)));
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.INTEGER.equals(type) || CommonTypes.NUMBER.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
}
