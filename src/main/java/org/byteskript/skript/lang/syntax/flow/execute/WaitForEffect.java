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
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.flow.lambda.SupplierSection;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Documentation(
    name = "Wait For",
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
public class WaitForEffect extends ControlEffect {
    
    public WaitForEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wait for %Executable%");
    }
    
    @ForceExtract
    public static Object getLock() {
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Unable to put non-script thread to sleep.");
        return thread.lock;
    }
    
    @ForceExtract
    public static void unlock() {
        final ScriptThread thread = ((ScriptThread) Thread.currentThread());
        synchronized (thread.lock) {
            thread.lock.notify();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("wait for ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final ElementTree tree = context.getCompileCurrent();
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        final MethodBuilder method = context.getMethod();
        this.writeCall(method, Thread.class.getMethod("currentThread"), context);
        final String location = new Type(ScriptThread.class).internalName();
        final PreVariable store = new PreVariable("thread");
        context.forceUnspecVariable(store);
        final int variable = context.slotOf(store);
        tree.metadata.put("variable", variable);
        method.writeCode((writer, visitor) -> {
            visitor.visitTypeInsn(192, location);
            visitor.visitInsn(Opcodes.DUP);
            visitor.visitVarInsn(Opcodes.ASTORE, variable);
            visitor.visitFieldInsn(180, location, "lock", "Ljava/lang/Object;");
            visitor.visitInsn(Opcodes.DUP);
            visitor.visitInsn(Opcodes.MONITORENTER);
        });
        final int index = context.getLambdaIndex();
        context.increaseLambdaIndex();
        final String name = "lambda$L" + index;
        final MethodBuilder child = context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC | 0x00001000)
            .setReturnType(new Type(void.class));
        SupplierSection.extractVariables(context, method, child);
        context.setMethod(child);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final int variable = (int) tree.metadata.get("variable");
        final String location = new Type(ScriptThread.class).internalName();
        final MethodBuilder method;
        {
            final MethodBuilder child = context.getMethod();
            this.writeCall(child, RunEffect.class.getMethod("run", Object.class), context);
            child.writeCode((writer, visitor) -> {
                visitor.visitVarInsn(Opcodes.ALOAD, variable);
                visitor.visitTypeInsn(192, location);
                visitor.visitFieldInsn(180, location, "lock", "Ljava/lang/Object;");
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitInsn(Opcodes.MONITORENTER);
                visitor.visitMethodInsn(182, "java/lang/Object", "notify", "()V", false);
                visitor.visitInsn(Opcodes.MONITOREXIT);
            });
            this.writeCall(child, WaitForEffect.class.getMethod("unlock"), context);
            child.writeCode(WriteInstruction.returnEmpty());
            final String internal = context.getType().internalName();
            method = context.getTriggerMethod();
            context.setMethod(method);
            final MethodErasure runnable = child.getErasure();
            final MethodErasure creator = new MethodErasure(CommonTypes.RUNNABLE, "run", child.getErasure()
                .parameterTypes());
            final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
            method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("run", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()V"), new Handle(6, internal, runnable.name(), runnable.getDescriptor(), false), org.objectweb.asm.Type.getType("()V")));
        }
        final Method target = ExtractedSyntaxCalls.class.getMethod("runOnAsyncThread", Runnable.class);
        this.writeCall(method, target, context);
        method.writeCode((writer, visitor) -> {
            visitor.visitInsn(Opcodes.DUP);
        });
        context.getMethod();
        this.writeCall(method, Object.class.getMethod("wait"), context);
        method.writeCode((writer, visitor) -> visitor.visitInsn(Opcodes.MONITOREXIT));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.RUN;
    }
    
}
