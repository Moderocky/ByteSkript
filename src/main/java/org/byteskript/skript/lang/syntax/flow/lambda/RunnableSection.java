/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.lambda;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
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
public class RunnableSection extends ExtractedSection {
    
    public RunnableSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new runnable");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.RUNNABLE;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains(" new runnable")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.RUNNABLE.equals(type) || CommonTypes.EXECUTABLE.equals(type);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        if (!context.isSectionHeader())
            throw new ScriptCompileError(context.lineNumber(), "Runnable has no body section.");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        super.compile(context, match);
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        final MethodBuilder method = context.getMethod();
        final int index = context.getLambdaIndex();
        final int load = context.getVariableCount();
        context.increaseLambdaIndex();
        final String internal = context.getType().internalName();
        final String name = "lambda$L" + index;
        final MethodBuilder child = context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC | 0x00001000)
            .setReturnType(new Type(void.class));
        for (int i = 0; i < load; i++) {
            child.addParameter(CommonTypes.OBJECT);
            method.writeCode(WriteInstruction.loadObject(i));
        }
        final MethodErasure target = child.getErasure();
        final MethodErasure creator = new MethodErasure(CommonTypes.RUNNABLE, "run", child.getErasure()
            .parameterTypes());
        final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
        this.addSkipInstruction(context, c -> c.setMethod(child));
        method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("run", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()V"), new Handle(6, internal, target.name(), target.getDescriptor(), false), org.objectweb.asm.Type.getType("()V")));
    }
    
    
}
