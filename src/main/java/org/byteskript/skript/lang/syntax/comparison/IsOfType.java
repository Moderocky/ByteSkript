/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.comparison;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.RelationalExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.type.TypeExpression;

import java.lang.reflect.Method;

@Documentation(
    name = "Is of Type",
    description = "Check whether an object is of the given type.",
    examples = {
        "assert 1 is a number",
        """
            if {var} is a string:
                print {var}
                """
    }
)
public class IsOfType extends RelationalExpression {
    
    public IsOfType() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "%Object% (is|are) a[n] %Type%");
        try {
            handlers.put(StandardHandlers.FIND, this.getClass().getMethod("check", Object.class, Object.class));
            handlers.put(StandardHandlers.GET, this.getClass().getMethod("check", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" is a")
            && !thing.contains(" are a")
        ) return null;
        return super.match(thing, context);
    }
    
    @ForceExtract
    public static Boolean check(Object object, Object type) {
        if (type instanceof Class cls) return cls.isInstance(object);
        return false;
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
