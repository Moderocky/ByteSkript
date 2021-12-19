package mx.kenzie.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.syntax.RelationalExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.generic.TypeExpression;

import java.lang.reflect.Method;

public class IsArray extends RelationalExpression {
    
    public IsArray() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is|are) a[n] array");
        try {
            handlers.put(StandardHandlers.FIND, this.getClass().getMethod("check", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith("array")) return null;
        return super.match(thing, context);
    }
    
    @ForceExtract
    public static Boolean check(Object object) {
        if (object == null) return false;
        return (object.getClass().isArray());
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final ElementTree tree = context.getCompileCurrent().nested()[1];
        if (tree.current() instanceof TypeExpression) {
            tree.compile = false;
        }
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final ElementTree tree = context.getCompileCurrent();
        if (tree.nested()[1].current() instanceof TypeExpression expression) {
            final String string = match.groups()[1];
            final Type type = expression.getType(string, context);
            method.writeCode(WriteInstruction.instanceOf(type));
            method.writeCode(WriteInstruction.invokeStatic(Boolean.class.getMethod("valueOf", boolean.class)));
        } else {
            final Method target = handlers.get(StandardHandlers.FIND);
            assert target != null;
            this.writeCall(method, target, context);
        }
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.BOOLEAN;
    }
    
}
