/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.timing;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.time.Duration;

@Documentation(
    name = "Minutes",
    description = """
        A timespan in minutes.
        """,
    examples = {
        """
            set {var} to 5 minutes
            wait 5 minutes
                """
    }
)
public class MinutesExpression extends SimpleExpression {
    
    public MinutesExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Number% min[ute][s]");
        try {
            handlers.put(StandardHandlers.FIND, this.getClass().getMethod("find", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.DURATION.equals(type) || CommonTypes.OBJECT.equals(type) || super.allowAsInputFor(type);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.FIND);
        assert target != null;
        this.writeCall(method, target, context);
        context.setState(CompileState.STATEMENT);
    }
    
    @ForceExtract
    public static Object find(Object object) {
        if (!(object instanceof Number)) {
            throw new ScriptRuntimeError("Timespan expression requires number.");
        } else {
            final Number number = (Number) object;
            return Duration.ofMinutes(number.longValue());
        }
    }
    
}
