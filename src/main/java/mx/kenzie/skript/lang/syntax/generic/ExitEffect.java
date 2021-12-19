package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

public class ExitEffect extends Effect {
    
    public ExitEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "exit[ the] program");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("exit ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.push0());
        method.writeCode(WriteInstruction.invokeStatic(System.class.getMethod("exit", int.class)));
        context.setState(CompileState.CODE_BODY);
    }
    
}
