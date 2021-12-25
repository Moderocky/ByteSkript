/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.OperatorHandler;
import org.byteskript.skript.runtime.type.AtomicVariable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

/**
 * The function call-site class compiler.
 * This cannot use {@link mx.kenzie.foundation} because it is required for
 * use in environments where BSC is not available.
 */
public class BridgeCompiler {
    
    protected final MethodHandles.Lookup lookup;
    protected final String owner;
    protected final MethodType source;
    protected final Method target;
    protected final String location;
    protected Class<?> generated;
    
    public BridgeCompiler(MethodHandles.Lookup lookup, String owner, MethodType source, Method target) {
        this.lookup = lookup;
        this.owner = owner;
        this.source = source;
        this.target = target;
        this.location = owner + "$" + "Bridge" + new Random().nextInt(100000, 999999);
    }
    
    public Class<?> createClass()
        throws IllegalAccessException {
        final Class<?>[] arguments = source.parameterArray();
        final Class<?>[] parameters = target.getParameterTypes();
        final Class<?> expected = source.returnType();
        final Class<?> result = target.getReturnType();
        if (arguments.length != parameters.length)
            throw new ScriptRuntimeError("Function argument count did not match target parameter count.");
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Skript.JAVA_VERSION, 0x0001 | 0x1000, location, null, "java/lang/Object", null);
        final MethodVisitor visitor;
        final Type[] types = new Type[arguments.length];
        for (int i = 0; i < arguments.length; i++) types[i] = Type.getType(arguments[i]);
        visitor = writer.visitMethod(0x0001 | 0x0008 | 0x0040 | 0x1000, "bridge", Type.getMethodDescriptor(Type.getType(expected), types), null, null);
        visitor.visitCode();
        for (int i = 0; i < arguments.length; i++) { // assume no fat arguments ?
            final Class<?> argument = arguments[i];
            final Class<?> parameter = parameters[i];
            visitor.visitVarInsn(20 + this.instructionOffset(argument), i);
            this.boxAtomic(visitor, parameter);
            visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(this.getUnboxingType(parameter)));
            this.unbox(visitor, parameter);
        }
        this.invoke(visitor);
        this.box(visitor, result);
        visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(this.getWrapperType(expected)));
        visitor.visitInsn(171 + this.instructionOffset(expected));
        visitor.visitMaxs(Math.max(parameters.length + 1 + this.wideIndexOffset(parameters, result), 1), arguments.length);
        visitor.visitEnd();
        writer.visitEnd();
        this.generated = lookup.defineClass(writer.toByteArray());
        return generated;
    }
    
    public CallSite getCallSite()
        throws NoSuchMethodException, IllegalAccessException {
        final MethodHandle handle = lookup.findStatic(generated, "bridge", source);
        return new ConstantCallSite(handle);
    }
    
    //region Utilities
    protected void invoke(MethodVisitor visitor) {
        final boolean special = target.getDeclaringClass().isInterface();
        visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(target.getDeclaringClass()), target.getName(), Type.getMethodDescriptor(target), special);
    }
    
    protected void doTypeConversion(MethodVisitor visitor, Class<?> from, Class<?> to) {
        if (from == to) return;
        if (from == void.class || to == void.class) return;
        if (from.isPrimitive() && to.isPrimitive()) {
            final int opcode;
            if (from == float.class) {
                if (to == double.class) opcode = F2D;
                else if (to == long.class) opcode = F2L;
                else opcode = F2I;
            } else if (from == double.class) {
                if (to == float.class) opcode = D2F;
                else if (to == long.class) opcode = D2L;
                else opcode = D2I;
            } else if (from == long.class) {
                if (to == float.class) opcode = L2F;
                else if (to == double.class) opcode = L2D;
                else opcode = L2I;
            } else {
                if (to == float.class) opcode = I2F;
                else if (to == double.class) opcode = I2D;
                else if (to == byte.class) opcode = I2B;
                else if (to == short.class) opcode = I2S;
                else if (to == char.class) opcode = I2C;
                else opcode = I2L;
            }
            visitor.visitInsn(opcode);
        } else if (from.isPrimitive() ^ to.isPrimitive()) {
            throw new IllegalArgumentException("Type wrapping is currently unsupported due to side-effects: '" + from.getSimpleName() + "' -> '" + to.getSimpleName() + "'");
        } else visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(to));
    }
    
    protected Class<?> getUnboxingType(Class<?> primitive) {
        if (!primitive.isPrimitive()) return primitive;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == void.class) return Void.class;
        if (primitive == char.class) return Character.class;
        return Number.class;
    }
    
    protected Class<?> getWrapperType(Class<?> primitive) {
        if (!primitive.isPrimitive()) return primitive;
        if (primitive == byte.class) return Byte.class;
        if (primitive == short.class) return Short.class;
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == float.class) return Float.class;
        if (primitive == double.class) return Double.class;
        if (primitive == char.class) return Character.class;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == void.class) return Void.class;
        return primitive;
    }
    
    protected void boxAtomic(MethodVisitor visitor, Class<?> parameter) {
        if (parameter == AtomicVariable.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(AtomicVariable.class), "wrap", "(Ljava/lang/Object;)" + Type.getDescriptor(AtomicVariable.class), false);
        else
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(AtomicVariable.class), "unwrap", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
    }
    
    protected void unbox(MethodVisitor visitor, Class<?> parameter) {
        final String source = Type.getInternalName(OperatorHandler.class);
        if (parameter == byte.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxB", "(Ljava/lang/Number;)B", false);
        if (parameter == short.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxS", "(Ljava/lang/Number;)S", false);
        if (parameter == int.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxI", "(Ljava/lang/Number;)I", false);
        if (parameter == long.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxJ", "(Ljava/lang/Number;)J", false);
        if (parameter == float.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxF", "(Ljava/lang/Number;)F", false);
        if (parameter == double.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unboxD", "(Ljava/lang/Number;)D", false);
        if (parameter == boolean.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unbox", "(Ljava/lang/Boolean;)Z", false);
        if (parameter == char.class)
            visitor.visitMethodInsn(INVOKESTATIC, source, "unbox", "(Ljava/lang/Character;)C", false);
    }
    
    protected void box(MethodVisitor visitor, Class<?> value) {
        if (value == byte.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
        if (value == short.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
        if (value == int.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
        if (value == long.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
        if (value == float.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
        if (value == double.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
        if (value == boolean.class)
            visitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
        if (value == void.class)
            visitor.visitInsn(ACONST_NULL);
    }
    
    protected int wideIndexOffset(Class<?>[] params, Class<?> ret) {
        int i = 0;
        for (Class<?> param : params) {
            i += wideIndexOffset(param);
        }
        return Math.max(i, wideIndexOffset(ret));
    }
    
    protected int wideIndexOffset(Class<?> thing) {
        if (thing == long.class || thing == double.class) return 1;
        return 0;
    }
    
    protected int instructionOffset(Class<?> type) {
        if (type == int.class) return 1;
        if (type == boolean.class) return 1;
        if (type == byte.class) return 1;
        if (type == short.class) return 1;
        if (type == long.class) return 2;
        if (type == float.class) return 3;
        if (type == double.class) return 4;
        if (type == void.class) return 6;
        return 5;
    }
    //endregion
    
}
