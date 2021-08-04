package mx.kenzie.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;

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
    
}
