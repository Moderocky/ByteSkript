package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.WriteInstruction;

public record PreVariable(String name) {
    
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
    
    @Override
    public boolean equals(Object o) {
        return (this == o);
    }
}
