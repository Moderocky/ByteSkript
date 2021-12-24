package org.byteskript.skript.lang.syntax.event;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.threading.ScriptThread;

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
