/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.execute;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprRunnableSection;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprSupplierSection;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Documentation(
    name = "Run in Background",
    description = """
        Runs the given executable in the background.
        Background processes ignore waits from the current process.
        """,
    examples = {
        """
            run {function} in the background
            run {runnable} in the background
                    """
    }
)
public class EffectRunAsync extends ControlEffect {
    
    public EffectRunAsync() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "run %Executable% (async[hronously]|in [the] background)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("run ")) return null;
        if (!thing.contains(" async") && !thing.endsWith(" background")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final ElementTree tree = context.getCompileCurrent();
        if (tree.nested()[0].current() instanceof ExprRunnableSection) return;
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        final MethodBuilder method = context.getMethod();
        final int index = context.getLambdaIndex();
        context.increaseLambdaIndex();
        final String name = "lambda$L" + index;
        final MethodBuilder child = context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC | 0x00001000)
            .setReturnType(new Type(void.class));
        ExprSupplierSection.extractVariables(context, method, child);
        tree.metadata.put("method", method);
        context.setMethod(child);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final MethodBuilder method;
        if (tree.nested()[0].current() instanceof ExprRunnableSection) {
            method = context.getMethod();
        } else {
            final MethodBuilder child = context.getMethod();
            final Method target = EffectRun.class.getMethod("run", Object.class);
            this.writeCall(child, target, context);
            child.writeCode(WriteInstruction.returnEmpty());
            final String internal = context.getType().internalName();
            method = (MethodBuilder) tree.metadata.get("method");
            context.setMethod(method);
            final MethodErasure runnable = child.getErasure();
            final MethodErasure creator = new MethodErasure(CommonTypes.RUNNABLE, "run", child.getErasure()
                .parameterTypes());
            final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
            method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("run", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()V"), new Handle(6, internal, runnable.name(), runnable.getDescriptor(), false), org.objectweb.asm.Type.getType("()V")));
        }
        final Method target = ExtractedSyntaxCalls.class.getMethod("runOnAsyncThread", Runnable.class);
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
