/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.byteskript.skript.compiler.BridgeCompiler;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Skript;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Description("""
    The meta-factory handles building call-sites at runtime.
    Currently, Java's LambdaMetafactory is used for building lambda call-sites.
    
    Methods in this are used by name for dynamic invocation.
    """)
@SuppressWarnings("unused")
public class Metafactory {
    
    @Description("""
        Accesses the bridge compiler to write a bridge stub for a function lookup call.
        
        This may be switched to a dual execution model in future versions to make use of lazy compiling.
        """)
    public static CallSite createBridge(MethodHandles.Lookup caller, String name, MethodType type, String source, Class<?> owner, Class<?>... parameters)
        throws Exception {
        final Method target = findTarget(caller, name, owner, parameters);
        final BridgeCompiler compiler = new BridgeCompiler(caller, source, type, target);
        final Class<?> generated = compiler.createClass();
        if (generated == null)
            throw new ScriptRuntimeError("Unable to generate function dynamic bridge during bootstrap phase.");
        return compiler.getCallSite();
    }
    
    private static Method findTarget(MethodHandles.Lookup caller, String name, Class<?> owner, Class<?>... parameters)
        throws Exception {
        final Method[] methods = owner.getMethods();
        for (final Method method : methods) {
            if (!method.getName().equals(name)) continue;
            if (Arrays.equals(method.getParameterTypes(), parameters)) return method;
        }
        for (final Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers())) continue; // can only hit statics for now
            if (!method.getName().equals(name)) continue;
            if (parameters.length == method.getParameterCount()) return method;
        }
        throw new ScriptRuntimeError("Unable to find function '" + name + Arrays.toString(parameters).replace('[', '(')
            .replace(']', ')') + "'");
    }
    
    @Ignore
    public static Object callFunction(String name, Object target, Object[] parameters) {
        final MethodAccessor<Object> accessor = Mirror.of(target).useProvider(Skript.findLoader())
            .method(name, parameters);
        if (accessor == null) throw new ScriptRuntimeError("Unable to find function '" + name + "' from " + target);
        return accessor.invoke(parameters);
    }
    
    @Ignore
    public static CallSite lambda(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    @Ignore
    public static CallSite joinStrings(MethodHandles.Lookup caller) throws Exception {
        final MethodType type = MethodType.methodType(String.class, String[].class);
        final MethodHandle handle = caller
            .findStatic(OperatorHandler.class, "concat", type)
            .withVarargs(true);
        return new ConstantCallSite(handle);
    }
    
}
