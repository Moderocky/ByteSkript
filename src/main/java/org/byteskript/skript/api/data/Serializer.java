/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.data;

import java.nio.ByteBuffer;

public abstract class Serializer {
    
    public abstract long getAllocationSize(Object... objects);
    
    public abstract void store(final ByteBuffer buffer, Object... objects);
    
}
