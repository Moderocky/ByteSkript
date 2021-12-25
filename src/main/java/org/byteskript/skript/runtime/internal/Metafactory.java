/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.compiler.BridgeCompiler;
import org.byteskript.skript.error.ScriptRuntimeError;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class Metafactory {
    
    public static CallSite createBridge(MethodHandles.Lookup caller, String name, MethodType type, String source, Class<?> owner, Class<?>... parameters)
        throws Exception {
        final Method target = findTarget(caller, name, owner, parameters);
        final BridgeCompiler compiler = new BridgeCompiler(caller, source, type, target);
        final Class<?> generated = compiler.createClass();
        if (generated == null)
            throw new ScriptRuntimeError("Unable to generate function dynamic bridge during bootstrap phase.");
        return compiler.getCallSite();
    }
    
    public static CallSite lambda(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite joinStrings(MethodHandles.Lookup caller) throws Exception {
        final MethodType type = MethodType.methodType(String.class, String[].class);
        final MethodHandle handle = caller
            .findStatic(OperatorHandler.class, "concat", type)
            .withVarargs(true);
        return new ConstantCallSite(handle);
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
            System.out.println(method); // todo
            if (!method.getName().equals(name)) continue;
            if (parameters.length == method.getParameterCount()) return method;
            else System.out.println(method); // todo
        }
        throw new ScriptRuntimeError("Unable to find function '" + name + Arrays.toString(parameters).replace('[', '(')
            .replace(']', ')') + "'");
    }
    
}
