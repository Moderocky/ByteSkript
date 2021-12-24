/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.WriteInstruction;

public final class PreVariable {
    private final String name;
    public boolean parameter;
    public boolean internal;
    public boolean atomic;
    
    public PreVariable(String name) {
        if (name == null) this.name = "EMPTY";
        else this.name = name;
    }
    
    public WriteInstruction load(final int slot) {
        return WriteInstruction.loadObject(slot);
    }
    
    public WriteInstruction store(final int slot) {
        return WriteInstruction.storeObject(slot);
    }
    
    public boolean skipPreset() {
        return parameter || internal;
    }
    
    @Override
    public String toString() {
        return "PreVariable[" +
            "name=" + name + ']';
    }
    
    public String name() {
        return name;
    }
    
}
