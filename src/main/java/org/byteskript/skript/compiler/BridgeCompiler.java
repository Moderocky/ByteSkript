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

/**
 * The function call-site class compiler.
 * This cannot use {@link mx.kenzie.foundation} because it is required for
 * use in environments where BSC is not available.
 */
public class BridgeCompiler {
    
    private static volatile int counter = 0;
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
        synchronized (BridgeCompiler.class) {
            this.location = owner + "$" + "Bridge" + ++counter;
        }
    }
    
    public Class<?> createClass()
        throws IllegalAccessException {
        final Class<?>[] arguments = source.parameterArray();
        final Class<?>[] parameters = target.getParameterTypes();
        final Class<?> expected = source.returnType();
        final Class<?> result = target.getReturnType();
        if (arguments.length != parameters.length && !target.isVarArgs())
            throw new ScriptRuntimeError("Function argument count did not match target parameter count.");
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Skript.JAVA_VERSION, 0x0001 | 0x1000, location, null, "java/lang/Object", null);
        final MethodVisitor visitor;
        final Type[] types = new Type[arguments.length];
        for (int i = 0; i < arguments.length; i++) types[i] = Type.getType(arguments[i]);
        visitor = writer.visitMethod(0x0001 | 0x0008 | 0x0040 | 0x1000, "bridge", Type.getMethodDescriptor(Type.getType(expected), types), null, null);
        visitor.visitCode();
        final int length;
        if (target.isVarArgs()) length = parameters.length - 1;
        else length = arguments.length;
        this.extractSimpleArguments(length, arguments, parameters, visitor);
        this.extractVarArguments(arguments, parameters, visitor, length);
        this.invoke(visitor);
        if (result == void.class) {
            visitor.visitInsn(1);
            visitor.visitInsn(176);
        } else {
            this.box(visitor, result);
            visitor.visitTypeInsn(192, Type.getInternalName(this.getWrapperType(expected)));
            visitor.visitInsn(171 + this.instructionOffset(expected));
        }
        final int stack;
        if (target.isVarArgs())
            stack = Math.max(parameters.length + 1 + this.wideIndexOffset(parameters, result), 1) + 4;
        else stack = Math.max(parameters.length + 1 + this.wideIndexOffset(parameters, result), 1);
        visitor.visitMaxs(stack, arguments.length);
        visitor.visitEnd();
        writer.visitEnd();
        this.generated = lookup.defineClass(writer.toByteArray());
        return generated;
    }
    
    private void extractVarArguments(Class<?>[] arguments, Class<?>[] parameters, MethodVisitor visitor, int length) {
        if (!target.isVarArgs()) return;
        final int remaining = arguments.length - length;
        final Class<?> array = parameters[parameters.length - 1];
        final Class<?> parameter = array.getComponentType();
        visitor.visitIntInsn(16, remaining);
        visitor.visitTypeInsn(189, Type.getInternalName(parameter));
        for (int i = 0; i < remaining; i++) {
            visitor.visitInsn(89);
            visitor.visitIntInsn(16, i);
            final Class<?> argument = arguments[i + length];
            visitor.visitVarInsn(20 + this.instructionOffset(argument), i + length);
            this.boxAtomic(visitor, parameter);
            this.conform(visitor, parameter);
            visitor.visitTypeInsn(192, Type.getInternalName(this.getUnboxingType(parameter)));
            this.unbox(visitor, parameter);
            visitor.visitInsn(83);
        }
    }
    
    private void extractSimpleArguments(int length, Class<?>[] arguments, Class<?>[] parameters, MethodVisitor visitor) {
        for (int i = 0; i < length; i++) { // assume no fat arguments ?
            final Class<?> argument = arguments[i];
            final Class<?> parameter = parameters[i];
            visitor.visitVarInsn(20 + this.instructionOffset(argument), i);
            this.boxAtomic(visitor, parameter);
            this.conform(visitor, parameter);
            visitor.visitTypeInsn(192, Type.getInternalName(this.getUnboxingType(parameter)));
            this.unbox(visitor, parameter);
        }
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
    
    protected void boxAtomic(MethodVisitor visitor, Class<?> parameter) {
        if (parameter == AtomicVariable.class)
            visitor.visitMethodInsn(184, Type.getInternalName(AtomicVariable.class), "wrap", "(Ljava/lang/Object;)" + Type.getDescriptor(AtomicVariable.class), false);
        else
            visitor.visitMethodInsn(184, Type.getInternalName(AtomicVariable.class), "unwrap", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
    }
    
    protected void conform(MethodVisitor visitor, Class<?> parameter) {
        if (parameter == Object.class || parameter == AtomicVariable.class) return;
        visitor.visitLdcInsn(Type.getObjectType(Type.getInternalName(this.getUnboxingType(parameter))));
        visitor.visitMethodInsn(184, "org/byteskript/skript/runtime/internal/ExtractedSyntaxCalls", "convert", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
    }
    
    protected Class<?> getUnboxingType(Class<?> primitive) {
        if (!primitive.isPrimitive()) return primitive;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == void.class) return Void.class;
        if (primitive == char.class) return Character.class;
        return Number.class;
    }
    
    protected void unbox(MethodVisitor visitor, Class<?> parameter) {
        final String source = Type.getInternalName(OperatorHandler.class);
        if (parameter == byte.class)
            visitor.visitMethodInsn(184, source, "unboxB", "(Ljava/lang/Number;)B", false);
        if (parameter == short.class)
            visitor.visitMethodInsn(184, source, "unboxS", "(Ljava/lang/Number;)S", false);
        if (parameter == int.class)
            visitor.visitMethodInsn(184, source, "unboxI", "(Ljava/lang/Number;)I", false);
        if (parameter == long.class)
            visitor.visitMethodInsn(184, source, "unboxJ", "(Ljava/lang/Number;)J", false);
        if (parameter == float.class)
            visitor.visitMethodInsn(184, source, "unboxF", "(Ljava/lang/Number;)F", false);
        if (parameter == double.class)
            visitor.visitMethodInsn(184, source, "unboxD", "(Ljava/lang/Number;)D", false);
        if (parameter == boolean.class)
            visitor.visitMethodInsn(184, source, "unbox", "(Ljava/lang/Boolean;)Z", false);
        if (parameter == char.class)
            visitor.visitMethodInsn(184, source, "unbox", "(Ljava/lang/Character;)C", false);
    }
    
    //region Utilities
    protected void invoke(MethodVisitor visitor) {
        final boolean special = target.getDeclaringClass().isInterface();
        visitor.visitMethodInsn(184, Type.getInternalName(target.getDeclaringClass()), target.getName(), Type.getMethodDescriptor(target), special);
    }
    
    protected void box(MethodVisitor visitor, Class<?> value) {
        if (value == void.class) visitor.visitInsn(1);
        if (!value.isPrimitive()) return;
        final Class<?> wrapper = this.getWrapperType(value);
        final String descriptor = "(" + Type.getDescriptor(value) + ")" + Type.getDescriptor(wrapper);
        visitor.visitMethodInsn(184, Type.getInternalName(wrapper), "valueOf", descriptor, false);
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
    
    public CallSite getCallSite()
        throws NoSuchMethodException, IllegalAccessException {
        final MethodHandle handle = lookup.findStatic(generated, "bridge", source);
        return new ConstantCallSite(handle);
    }
    //endregion
    
}
