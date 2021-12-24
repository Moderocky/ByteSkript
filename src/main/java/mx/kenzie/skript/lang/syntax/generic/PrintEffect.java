package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.io.PrintStream;

public class PrintEffect extends Effect {
    
    public PrintEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "print %Object%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.getField(System.class.getField("out")));
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.invokeVirtual(PrintStream.class.getMethod("println", Object.class)));
        context.setState(CompileState.CODE_BODY);
    }
    
}
