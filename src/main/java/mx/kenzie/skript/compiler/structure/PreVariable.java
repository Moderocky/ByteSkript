package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.WriteInstruction;

public final class PreVariable {
    private final String name;
    public boolean parameter;
    
    public PreVariable(String name) {
        this.name = name;
    }
    
    public WriteInstruction load(final int slot) {
        return WriteInstruction.loadObject(slot);
    }
    
    public WriteInstruction store(final int slot) {
        return WriteInstruction.storeObject(slot);
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
