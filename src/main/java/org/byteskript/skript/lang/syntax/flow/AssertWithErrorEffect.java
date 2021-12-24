package org.byteskript.skript.lang.syntax.flow;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.error.ScriptAssertionError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class AssertWithErrorEffect extends Effect {
    
    public AssertWithErrorEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "assert %Boolean%: %String%");
        handlers.put(StandardHandlers.RUN, findMethod(AssertWithErrorEffect.class, "assertion", Object.class, Object.class, Class.class, int.class));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(StandardHandlers.RUN);
        assert target != null;
        final int line = context.lineNumber();
        method.writeCode(WriteInstruction.loadClassConstant(method.finish().getType()));
        method.writeCode(WriteInstruction.push(line));
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("assert ")) return null;
        if (!thing.contains(": ")) return null;
        return super.match(thing, context);
    }
    
    @ForceExtract
    public static void assertion(Object object, Object message, Class<?> script, int line) {
        if (object == null)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Boolean boo && !boo)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Number number && number.intValue() == 0)
            throw new ScriptAssertionError(script, line, message + "");
    }
    
}
