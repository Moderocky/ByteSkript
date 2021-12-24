/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Method;

public record Executable(Type owner, MethodErasure erasure) {
    
    public Executable(Method method) {
        this(new Type(method.getDeclaringClass()), new MethodErasure(method));
    }
    
}
