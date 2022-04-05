/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.*;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.internal.Metafactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

@Documentation(
    name = "Local Function",
    description = """
        A function attached to an object.
        This can have special behaviour specific to the object it is run for.
        """,
    examples = {
        """
            set {sides} to get_sides() from {square}
                """
    }
)
public class ExprFunctionProperty extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("(?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")\\((?<params>.*)\\) from (?<object>.+)");
    
    public ExprFunctionProperty() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "function(...) from %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(") from ")) return null;
        if (!thing.contains("(")) return null;
        return createMatch(thing, context);
    }
    
    private Pattern.Match createMatch(String thing, Context context) {
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        final String name = matcher.group("name");
        final String params = matcher.group("params");
        final Type[] parameters = getParams(params);
        final Matcher dummy = java.util.regex.Pattern.compile(buildDummyPattern(name, parameters.length))
            .matcher(thing);
        dummy.find();
        final List<Type> types = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            types.add(CommonTypes.OBJECT);
        }
        types.add(CommonTypes.OBJECT);
        return new Pattern.Match(dummy, new FunctionDetails(name, parameters), types.toArray(new Type[0]));
    }
    
    private Type[] getParams(String params) {
        if (params.isBlank()) return new Type[0];
        int nest = 0;
        final List<Type> types = new ArrayList<>();
        int count = 1;
        boolean atomic = false;
        for (char c : params.toCharArray()) {
            if (c == '(') nest++;
            else if (c == ')') nest--;
            else if (c == '@' && nest < 1) atomic = true;
            else if (c == ',' && nest < 1) {
                count++;
                if (atomic) types.add(CommonTypes.ATOMIC);
                else types.add(CommonTypes.OBJECT);
                atomic = false;
            }
        }
        if (atomic) types.add(CommonTypes.ATOMIC);
        else types.add(CommonTypes.OBJECT);
        return types.toArray(new Type[0]);
    }
    
    private String buildDummyPattern(String name, int params) {
        final StringBuilder builder = new StringBuilder()
            .append(name).append("\\(");
        if (params > 0) {
            for (int i = 0; i < params; i++) {
                if (i > 0) builder.append(", ");
                builder.append("(.+)");
            }
        }
        return builder.append("\\) from (.+)").toString();
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        for (final ElementTree tree : context.getCompileCurrent().nested()) {
            tree.takeAtomic = true;
        }
        final String name = ((FunctionDetails) match.meta()).name();
        context.getMethod().writeCode(WriteInstruction.loadConstant(name)); // ldc name
        final ElementTree[] nested = context.getCompileCurrent().nested();
        if (nested.length < 2) return;
        final ElementTree tree = nested[nested.length - 1]; // swap object order before array pack
        System.arraycopy(nested, 0, nested, 1, nested.length - 1);
        nested[0] = tree;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final FunctionDetails details = match.meta();
        assert details != null;
        final int expected = context.getCompileCurrent().nested().length - 1; // DON'T pack source
        final ClassBuilder builder = context.getBuilder();
        final Type[] parameters = new Type[expected];
        Arrays.fill(parameters, CommonTypes.OBJECT);
        final MethodErasure erasure = new MethodErasure(CommonTypes.OBJECTS, "lambda$packArray", parameters);
        if (!builder.hasMatching(erasure)) {
            final MethodBuilder target = builder.addMatching(erasure)
                .setModifiers(0x00000002 | 0x00000008 | 0x00001000);
            target.writeCode(WriteInstruction.newArray(Object.class, parameters.length));
            for (int i = 0; i < parameters.length; i++) {
                target.writeCode(WriteInstruction.duplicate());
                target.writeCode(WriteInstruction.loadObject(i));
                target.writeCode(WriteInstruction.arrayStoreObject(i));
            }
            target.writeCode(WriteInstruction.returnObject());
        }
        method.writeCode(WriteInstruction.invokeStatic(builder.getType(), erasure)); // pack array
        this.writeCall(method, findMethod(Metafactory.class, "callFunction", String.class, Object.class, Object[].class), context);
    }
    
    private record FunctionDetails(String name, Type[] arguments) {
    }
    
}
