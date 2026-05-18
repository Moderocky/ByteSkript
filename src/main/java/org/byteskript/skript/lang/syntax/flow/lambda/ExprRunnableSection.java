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
    name = "Runnable",
    description = """
        Creates a section of runnable code that can be stored.
        This can be run anywhere using the `run` effect.
        Variables used inside are frozen.
        """,
    examples = {
        """
            set {var} to a new runnable:
                print "hello"
            run {var}
            run a new runnable:
                print "bye"
                    """
    }
)
public class ExprRunnableSection extends ExtractedSection {
    
    public ExprRunnableSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new runnable");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" new runnable")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.RUNNABLE;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.RUNNABLE.equals(type) || CommonTypes.EXECUTABLE.equals(type);
    }

    @Override
    public MethodBuilder createExtractedMethod(Context context, Pattern.Match match) {
        final int index = context.getLambdaIndex();
        context.increaseLambdaIndex();
        final String name = "lambda$L" + index;

        return context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC)
            .setReturnType(new Type(void.class));
    }

    @Override
    public void buildInvoker(Context context, Pattern.Match match, MethodBuilder child) throws NoSuchMethodException {
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        final MethodBuilder method = context.getMethod();
        final String internal = context.getType().internalName();

        ExprSupplierSection.extractVariables(context, method, child);
        final MethodErasure target = child.getErasure();
        final MethodErasure creator = new MethodErasure(CommonTypes.RUNNABLE, "run", child.getErasure()
            .parameterTypes());
        final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
        method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("run", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()V"), new Handle(6, internal, target.name(), target.getDescriptor(), false), org.objectweb.asm.Type.getType("()V")));
    }
}
