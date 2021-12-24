package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Method;
import java.util.Objects;

public class NotEqual extends RelationalExpression {
    
    public NotEqual() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (isn't|is not|aren't|are not|≠|!=) %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" is")
            && !thing.contains(" are")
            && !thing.contains(" ≠ ")
            && !thing.contains(" != ")
        ) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        try {
            final MethodBuilder method = context.getMethod();
            assert method != null;
            final Method target = Objects.class.getDeclaredMethod("equals", Object.class, Object.class);
            method.writeCode(WriteInstruction.invokeStatic(target));
            method.writeCode((writer, visitor) -> {
                // Much faster method of inverting the boolean
                visitor.visitInsn(4);
                visitor.visitInsn(130);
            });
            method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
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
            Check whether the first number is greater than the second.""";
    }
    
    @Override
    public String[] examples() {
        return new String[]{
            "assert 4 is not 3",
            """
                if {var} != 6:
                    print "hello"
                    """
        };
    }
    
}
