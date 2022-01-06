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
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.GlobalVariableMap;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

@Documentation(
    name = "Global Variable",
    description = """
        A global reference variable, accessible from anywhere.
        This variable is accessed by name, and only a single copy exists.
        """,
    examples = """
        set {!var} to 5
        """
)
public class GlobalVariableExpression extends VariableExpression implements Referent {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\{(?<name>!" + SkriptLangSpec.IDENTIFIER + ")\\}");
    
    public GlobalVariableExpression() {
        super();
        try {
            handlers.put(StandardHandlers.GET, GlobalVariableMap.class.getMethod("getVariable", Object.class));
            handlers.put(StandardHandlers.FIND, GlobalVariableMap.class.getMethod("getVariable", Object.class));
            handlers.put(StandardHandlers.SET, GlobalVariableMap.class.getMethod("setVariable", Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, GlobalVariableMap.class.getMethod("deleteVariable", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 4) return null;
        if (thing.charAt(1) != '!') return null;
        if (thing.charAt(0) != '{') return null;
        if (!thing.endsWith("}")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) {
            context.getError().addHint(this, "Variable names must be alphanumeric (but allow _).");
            return null;
        }
        return new Pattern.Match(matcher);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final String name = match.matcher().group("name");
        method.writeCode(WriteInstruction.loadConstant(name));
        if (context.getHandlerMode().expectInputs())
            method.writeCode(WriteInstruction.swap()); // name, arg
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
}
