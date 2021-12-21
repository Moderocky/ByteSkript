package mx.kenzie.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.internal.Member;

public class DynamicFunctionExpression extends SimpleExpression {
    
    public DynamicFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION,
            "[the ]function %String%");
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
        assert method != null;
        method.writeCode(WriteInstruction.invokeStatic(Member.class.getMethod("getFunction", Object.class, String.class)));
    }
    
}
