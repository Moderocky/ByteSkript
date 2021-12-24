package org.byteskript.skript.api.data;

import java.nio.ByteBuffer;

public abstract class Serializer {
    
    public abstract long getAllocationSize(Object... objects);
    
    public abstract void store(final ByteBuffer buffer, Object... objects);
    
}
