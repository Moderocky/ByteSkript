package org.byteskript.skript.lang.syntax.variable;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.type.AtomicVariable;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

public class AtomicVariableExpression extends VariableExpression implements Referent {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\{(?<name>@" + SkriptLangSpec.IDENTIFIER + ")\\}");
    
    public AtomicVariableExpression() {
        super();
        try {
            handlers.put(StandardHandlers.GET, AtomicVariable.class.getMethod("get", Object.class));
            handlers.put(StandardHandlers.FIND, AtomicVariable.class.getMethod("get", Object.class));
            handlers.put(StandardHandlers.SET, AtomicVariable.class.getMethod("set", Object.class, Object.class));
            handlers.put(StandardHandlers.DELETE, AtomicVariable.class.getMethod("delete", Object.class));
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
        if (thing.length() < 4) return null;
        if (thing.charAt(1) != '@') return null;
        if (thing.charAt(0) != '{') return null;
        if (!thing.endsWith("}")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
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
        variable.atomic = true;
        final int slot = context.slotOf(variable);
        method.writeCode(variable.load(slot));
        context.setState(CompileState.STATEMENT);
        if (context.getCompileCurrent().takeAtomic) return;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
