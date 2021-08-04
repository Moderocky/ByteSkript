package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.error.ScriptParseError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class RemoveEffect extends ControlEffect {
    
    public RemoveEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "remove %Object% from %Referent%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) {
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 2;
        if (!(inputs[1].current() instanceof final Referent referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[1].current()
                .name() + "' cannot be removed from.");
        final Method target = referent.getHandler(StandardHandlers.REMOVE);
        if (target == null)
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be removed from.");
        inputs[1].compile = false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        final Referent referent = (Referent) inputs[1].current();
        final Method target = referent.getHandler(StandardHandlers.REMOVE);
        assert target != null;
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.REMOVE;
    }
    
}
