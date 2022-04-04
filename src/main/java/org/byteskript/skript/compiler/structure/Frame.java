/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.Type;

public interface Frame {
    
    int size();
    
    void add(Type... types);
    
    void remove(Type... types);
    
}
