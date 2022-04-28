/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

@FunctionalInterface
public interface OperatorFunction<A, B> {
    
    default OperatorFunction<B, A> opposite() {
        final OperatorFunction<A, B> function = this;
        return new OperatorFunction<>() {
            @Override
            public Object union2(A first, B second) throws Throwable {
                return function.union(first, second);
            }
            
            @Override
            public Object union(B first, A second) throws Throwable {
                return function.union2(first, second);
            }
            
            @Override
            public boolean reversible() {
                return function.reversible();
            }
        };
    }
    
    Object union(A first, B second) throws Throwable;
    
    default Object union2(B first, A second) throws Throwable {
        return this.union(second, first);
    }
    
    default boolean reversible() {
        return true;
    }
    
    enum Type {
        ADD, SUB, MUL, DIV
    }
    
    record Data(Type type, Class<?> first, Class<?> second) {}
    
}
