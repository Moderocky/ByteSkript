package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

public class JavaVersionExpression extends SimpleExpression {
    
    public JavaVersionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ]java version");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.INTEGER;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.INTEGER.equals(type) || CommonTypes.NUMBER.equals(type) || CommonTypes.OBJECT.equals(type);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.loadConstant(SkriptLangSpec.JAVA_VERSION.version));
        method.writeCode(WriteInstruction.invokeStatic(Integer.class.getMethod("valueOf", int.class)));
    }
    
}
