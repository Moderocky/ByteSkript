/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

public interface Instruction<Type> {
    
    void run() throws Throwable;
    
    default Type get() {
        return null;
    }
    
    default void runSafely() {
        try {
            this.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
