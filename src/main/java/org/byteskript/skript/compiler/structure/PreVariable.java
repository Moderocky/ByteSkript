/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.compiler.CommonTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PreVariable {
    private final String name;
    public boolean parameter;
    public boolean internal;
    public boolean atomic;
    public final List<Type> insight = new ArrayList<>();
    
    public PreVariable(String name) {
        if (name == null) this.name = "EMPTY";
        else this.name = name;
    }
    
    public boolean typeKnown() {
        return insight.size() == 1;
    }
    
    public Type getType() {
        if (insight.size() == 1) return insight.get(0);
        else return CommonTypes.OBJECT;
    }
    
    public void addTypeInsight(Type type) {
        this.insight.add(type);
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
