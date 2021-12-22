package mx.kenzie.skript.lang.syntax.timing;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.time.Duration;

public class WeeksExpression extends SimpleExpression {
    
    public WeeksExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Number% week[s]");
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
        if (!(object instanceof Number number))
            throw new ScriptRuntimeError("Timespan expression requires number.");
        return Duration.ofDays(number.longValue() * 7);
    }
    
}
