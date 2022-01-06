/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.runtime.internal.Bootstrapper;

public record Function(String name, Type provider, Type returnType, Type[] arguments, Type result, Type[] parameters) {
    
    public Function(String name, Type provider) {
        this(name, provider, CommonTypes.OBJECT);
    }
    
    public Function(String name, Type provider, Type returnType, Type... arguments) {
        this(name, provider, returnType, arguments, returnType, arguments);
    }
    
    public Function(Type provider, MethodErasure erasure) {
        this(erasure.name(), provider, erasure.returnType(), erasure.parameterTypes(), erasure.returnType(), erasure.parameterTypes());
    }
    
    public WriteInstruction invoke(String source) {
        final org.objectweb.asm.Type owner = org.objectweb.asm.Type.getType(provider.descriptorString());
        final org.objectweb.asm.Type[] types = convert(parameters);
        final org.objectweb.asm.Type blob = org.objectweb.asm.Type.getType(void.class);
        return WriteInstruction.invokeDynamic(CommonTypes.OBJECT, name, arguments, Bootstrapper.getBootstrapFunction(), source, owner, org.objectweb.asm.Type.getMethodDescriptor(blob, types));
    }
    
    private org.objectweb.asm.Type[] convert(Type... arguments) {
        final org.objectweb.asm.Type[] types = new org.objectweb.asm.Type[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            types[i] = org.objectweb.asm.Type.getType(arguments[i].descriptorString());
        }
        return types;
    }
    
    private org.objectweb.asm.Type convert(Type type) {
        return org.objectweb.asm.Type.getType(type.descriptorString());
    }
    
}
