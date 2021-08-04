package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.compiler.CommonTypes;

import java.util.Arrays;

public record Function(String name, Type provider) {
    
    public WriteInstruction invoke(int arguments) {
        final Type[] types = new Type[arguments];
        Arrays.fill(types, CommonTypes.OBJECT); // assert all types are actually object :)
        return WriteInstruction.invokeStatic(provider, CommonTypes.OBJECT, name, types);
    }
    
}
