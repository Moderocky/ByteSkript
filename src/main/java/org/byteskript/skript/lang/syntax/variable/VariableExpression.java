/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.variable;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.OperatorHandler;

import java.util.regex.Matcher;

@Documentation(
    name = "Local Variable",
    description = """
        A normal variable, local to this trigger.
        This variable exists only in the current trigger.
        Lambdas will use a frozen copy that does not reference the original.
        """,
    examples = """
        set {var} to 5
        """
)
public class VariableExpression extends SimpleExpression implements Referent {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\{(?<name>" + SkriptLangSpec.IDENTIFIER + ")\\}");
    
    public VariableExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "variable");
        try {
            handlers.put(StandardHandlers.ADD, OperatorHandler.class.getMethod("addObject", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 3) return null;
        if (thing.charAt(0) != '{') return null;
        if (thing.charAt(1) == '@') return null;
        if (thing.charAt(1) == '_') return null;
        if (thing.charAt(1) == '!') return null;
        if (!thing.endsWith("}")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) {
            context.getError().addHint(this, "Variable names must be alphanumeric (but allow _).");
            return null;
        }
        return new Pattern.Match(matcher);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = match.matcher().group("name");
        final PreVariable variable = context.getVariable(name);
        final int slot = context.slotOf(variable);
        if (context.getHandlerMode().equals(StandardHandlers.SET)) {
            method.writeCode(variable.store(slot));
        } else if (context.getHandlerMode().equals(StandardHandlers.GET)) {
            method.writeCode(variable.load(slot));
        } else if (context.getHandlerMode().equals(StandardHandlers.DELETE)) {
            method.writeCode(WriteInstruction.pushNull());
            method.writeCode(variable.store(slot));
        }
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
