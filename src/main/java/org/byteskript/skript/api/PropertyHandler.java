/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public record PropertyHandler(String name, HandlerType type, Type holder, Type value, Method method) {
    
    public PropertyHandler(HandlerType type, Method method, String name) {
        this(name, type, createHolder(method), createValue(type, method), method);
    }
    
    private static Type createValue(HandlerType type, Method method) {
        if (type.expectInputs()) {
            final Class<?>[] classes = method.getParameterTypes();
            assert classes.length > 0;
            return new Type(classes[classes.length - 1]);
        } else if (type.expectReturn()) {
            return new Type(method.getReturnType());
        } else {
            return new Type(void.class);
        }
    }
    
    private static Type createHolder(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            final Class<?>[] classes = method.getParameterTypes();
            assert classes.length > 0;
            return new Type(classes[0]);
        } else {
            return new Type(method.getDeclaringClass());
        }
    }
    
    
}
