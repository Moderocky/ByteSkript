/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.lambda;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.ExtractedSection;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

@Documentation(
    name = "Supplier",
    description = """
        Creates a section of runnable code that can be stored.
        This gives back a value using the `return` effect.
        This can be run anywhere using the `result of` expression.
        Variables used inside are frozen.
        """,
    examples = {
        """
            set {var} to a new supplier:
                return "hello"
            set {word} to result of {var}
            print result of a new supplier:
                return "bye"
            """
    }
)
public class ExprSupplierSection extends ExtractedSection {
    
    public ExprSupplierSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new supplier");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" new supplier")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.SUPPLIER;
    }

    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.SUPPLIER.equals(type) || CommonTypes.EXECUTABLE.equals(type);
    }

    @Override
    public MethodBuilder createExtractedMethod(Context context, Pattern.Match match) throws Throwable {
        final int index = context.getLambdaIndex();
        context.increaseLambdaIndex();
        final String name = "lambda$L" + index;
        return context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC)
            .setReturnType(CommonTypes.OBJECT);
    }

    @Override
    public void buildInvoker(Context context, Pattern.Match match, MethodBuilder child) throws Throwable {
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);

        final String internal = context.getType().internalName();
        final MethodBuilder method = context.getMethod();
        extractVariables(context, method, child);
        final MethodErasure target = child.getErasure();
        final MethodErasure creator = new MethodErasure(CommonTypes.SUPPLIER, "get", child.getErasure()
            .parameterTypes());
        final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
        method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("get", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()Ljava/lang/Object;"), new Handle(6, internal, target.name(), target.getDescriptor(), false), org.objectweb.asm.Type.getType("()Ljava/lang/Object;")));
    }
    
    public static void extractVariables(Context context, MethodBuilder method, MethodBuilder child) {
        for (final PreVariable variable : context.getVariables()) {
            if (!variable.internal) {
                child.addParameter(CommonTypes.OBJECT);
                final int slot = context.slotOf(variable);
                method.writeCode(variable.load(slot));
            }
        }
    }
}
