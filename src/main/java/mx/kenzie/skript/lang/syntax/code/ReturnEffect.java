package mx.kenzie.skript.lang.syntax.code;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.Effect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

public class ReturnEffect extends Effect {
    
    public ReturnEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "return %Object%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.returnObject());
        context.setState(CompileState.CODE_BODY);
    }
    
}
