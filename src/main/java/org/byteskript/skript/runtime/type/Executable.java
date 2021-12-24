package org.byteskript.skript.runtime.type;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;

import java.lang.reflect.Method;

public record Executable(Type owner, MethodErasure erasure) {
    
    public Executable(Method method) {
        this(new Type(method.getDeclaringClass()), new MethodErasure(method));
    }
    
}
