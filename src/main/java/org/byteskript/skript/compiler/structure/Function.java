/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.compiler.CommonTypes;

import java.util.Arrays;

public record Function(String name, Type provider) {
    
    public WriteInstruction invoke(int arguments) {
        final Type[] types = new Type[arguments];
        Arrays.fill(types, CommonTypes.OBJECT); // assert all types are actually object :)
        return WriteInstruction.invokeStatic(provider, CommonTypes.OBJECT, name, types);
    }
    
}
