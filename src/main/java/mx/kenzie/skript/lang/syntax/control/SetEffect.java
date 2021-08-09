package mx.kenzie.skript.lang.syntax.control;

import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.Referent;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.error.ScriptParseError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

public class SetEffect extends ControlEffect {
    
    public SetEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "set %Referent% to %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("set ") || !thing.contains(" to ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) {
        final ElementTree tree = context.getCompileCurrent();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 2;
        if (!(inputs[0].current() instanceof Referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be set.");
        inputs[0].compile = false;
        inputs[0].type = StandardHandlers.SET;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final ElementTree[] inputs = tree.nested();
        inputs[0].compile = true;
        inputs[0].compile(context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.SET;
    }
    
}
