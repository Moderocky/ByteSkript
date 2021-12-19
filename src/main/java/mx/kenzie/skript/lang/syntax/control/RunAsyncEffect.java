package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.Instruction;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.flow.RunnableSection;
import mx.kenzie.skript.lang.syntax.generic.VariableExpression;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class RunAsyncEffect extends ControlEffect {
    
    public RunAsyncEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable% (async[hronously]|in [the ]background)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        if (!thing.contains(" async") && !thing.contains("background")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        // final ElementTree tree = context.getLine().nested()[0];
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = Member.class.getMethod("runAsync", Object.class);
        this.writeCall(method, target, context);
        method.writeCode(WriteInstruction.pop());
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
