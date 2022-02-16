/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

@FunctionalInterface
public interface Converter<From, To> {
    
    To convert(From from);
    
    record Data(Class<?> from, Class<?> to) {}
    
}

