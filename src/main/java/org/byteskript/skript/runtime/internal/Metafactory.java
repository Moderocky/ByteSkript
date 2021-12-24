/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import java.lang.invoke.*;

public class Metafactory {
    
    
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
    
    
}
