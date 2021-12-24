package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.error.ScriptParseError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.variable.VariableExpression;

import java.lang.reflect.Method;

public class DeleteEffect extends ControlEffect {
    
    public DeleteEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "delete %Referent%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("delete ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 1;
        inputs[0].type = StandardHandlers.DELETE;
        if (inputs[0].current() instanceof VariableExpression)
            return; // variables have to handle their own deletion
        if (!(inputs[0].current() instanceof final Referent referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be deleted.");
        final Method target = referent.getHandler(StandardHandlers.DELETE);
        if (target == null)
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be deleted.");
        inputs[0].compile = false;
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        if (!(inputs[0].current() instanceof VariableExpression)) { // variables have to handle their own deletion
            final Referent referent = (Referent) inputs[0].current();
            final Method target = referent.getHandler(StandardHandlers.DELETE);
            assert target != null;
            this.writeCall(method, target, context);
        }
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.DELETE;
    }
    
}
