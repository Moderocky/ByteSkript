/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.error.ScriptBootstrapError;
import org.objectweb.asm.Handle;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

@Description("""
    The bootstrapper handles resolving call-sites for dynamic method calls.
    This is used by functions in conjunction with the meta-factory.
    
    Methods in this are used by name from dynamic invocation.
    
    This is an adapted copy of Krow's bootstrapper.
    """)
@GenerateExample
@SuppressWarnings("unused")
public final class Bootstrapper {
    
    @Description("""
        Gets the handle for a Skript function.
        """)
    public static Handle getBootstrapFunction() {
        try {
            return getHandle(Bootstrapper.class.getMethod("bootstrapFunction", MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Class.class, String.class));
        } catch (Throwable ex) {
            throw new ScriptBootstrapError(ex);
        }
    }
    
    private static Handle getHandle(final Method method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, new Type(method.getDeclaringClass()).internalName(), method.getName(), getDescriptor(new Type(method.getReturnType()), Type.of(method.getParameterTypes())), code == H_INVOKEINTERFACE);
    }
    
    private static String getDescriptor(final Type ret, final Type... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Type type : params) {
            builder.append(type.descriptorString());
        }
        builder
            .append(")")
            .append(ret.descriptorString());
        return builder.toString();
    }
    
    @Description("""
        Gets the handle for a regular method, used for writing a dynamic call-site.
        """)
    public static Handle getBootstrap(final boolean isDynamic, final boolean isPrivate) {
        try {
            if (isPrivate) {
                if (isDynamic)
                    return getHandle(Bootstrapper.class.getMethod("bootstrapPrivateDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                return getHandle(Bootstrapper.class.getMethod("bootstrapPrivate", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
            } else {
                if (isDynamic)
                    return getHandle(Bootstrapper.class.getMethod("bootstrapDynamic", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
                return getHandle(Bootstrapper.class.getMethod("bootstrap", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class));
            }
        } catch (Throwable ex) {
            throw new ScriptBootstrapError(ex);
        }
    }
    
    @Description("""
        This writes the call-site for invoking a Skript function from a script.
        
        The metafactory is invoked to create a new bridge stub.
        """)
    public static CallSite bootstrapFunction(MethodHandles.Lookup caller, String name, MethodType type, String source, Class<?> owner, String args)
        throws Exception {
        final org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(args);
        final Class<?>[] arguments = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            arguments[i] = getClass(types[i].getClassName());
        }
        return Metafactory.createBridge(caller, name, type, source, owner, arguments);
    }
    
    private static Class<?> getClass(String name) throws ClassNotFoundException {
        return switch (name) {
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "void" -> void.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            default -> Class.forName(name);
        };
    }
    
    @Description("Finds an existing call-site for a regular static method.")
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    @Description("Finds an existing call-site for a private static method.")
    public static CallSite bootstrapPrivate(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller).findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    @Description("Finds an existing call-site for a regular dynamic method.")
    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = caller.findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }
    
    @Description("Finds an existing call-site for a private dynamic method.")
    public static CallSite bootstrapPrivateDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = MethodHandles.privateLookupIn(owner, caller).findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapPrivateStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapPrivateStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapPrivateFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }
    
    @Description("""
        Finds an existing call-site for a static field.
        
        Rather than using the variable handle, this returns a method call-site.
        This allows the field to be treated like a method by the compiler.
        """)
    public static CallSite bootstrapPrivateFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }
    
}
