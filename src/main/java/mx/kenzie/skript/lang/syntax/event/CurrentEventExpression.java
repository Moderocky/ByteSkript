package mx.kenzie.skript.lang.syntax.event;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.threading.ScriptThread;

import java.lang.reflect.Method;

public class CurrentEventExpression extends SimpleExpression {
    
    public CurrentEventExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ][current ]event");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EVENT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = CurrentEventExpression.class.getMethod("get");
        this.writeCall(method, target, context);
    }
    
    @ForceExtract
    public static Event get() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) return null;
        return thread.event;
    }
    
}
