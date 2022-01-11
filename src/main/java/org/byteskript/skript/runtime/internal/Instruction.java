/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;

@Ignore
public interface Instruction<Type> {
    
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
    
    void run() throws Throwable;
    
}
