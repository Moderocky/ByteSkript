package mx.kenzie.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.internal.OperatorHandler;

import java.lang.reflect.Method;

public class Contains extends RelationalExpression {
    
    public Contains() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% contains %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" contains ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = OperatorHandler.class.getDeclaredMethod("contains", Object.class, Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            context.setState(CompileState.STATEMENT);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
    @Override
    public String description() {
        return """
            Check whether the first object contains the second.
            Applies to strings, lists and other collection types.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert \"hello\" contains \"h\"",
            """
                if {list} contains 3:
                    print "hello"
                    """
        };
    }
}
