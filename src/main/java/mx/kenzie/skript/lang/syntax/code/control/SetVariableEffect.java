package mx.kenzie.skript.lang.syntax.code.control;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.syntax.ControlEffect;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.PreVariable;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.util.regex.Matcher;

public class SetVariableEffect extends ControlEffect {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("set \\{(?<name>" + SkriptLangSpec.IDENTIFIER + ")\\} to .+");
    
    public SetVariableEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "set variable to %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("set {")) return null;
        final Matcher first = PATTERN.matcher(thing);
        if (!first.find()) return null;
        final String name = first.group("name");
        final String fake = "set \\\\{" + name + "\\\\} to %Object%";
        final Pattern pattern = new Pattern(new String[]{fake}, provider);
        return pattern.match(thing, context, name);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        assert match.meta() != null;
        final String name = (String) match.meta();
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final PreVariable variable = context.getVariable(name);
        final int slot = context.slotOf(variable);
        method.writeCode(variable.store(slot));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.SET;
    }
    
}
