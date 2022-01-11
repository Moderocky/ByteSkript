/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

import mx.kenzie.autodoc.api.note.Ignore;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

@SuppressWarnings({"removal"})
@Ignore
class UnsafeAccessor {
    
    private static final Unsafe UNSAFE;
    
    static {
        try {
            UNSAFE = java.security.AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe) f.get(null);
            });
        } catch (PrivilegedActionException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static Unsafe getUnsafe() {
        return UNSAFE;
    }
    
    @Deprecated
    protected static void graveyard(final Object object) {
        final Field[] fields = object.getClass().getDeclaredFields();
        synchronized (object) {
            UNSAFE.fullFence();
            for (final Field field : fields) {
                if (field.getType().isPrimitive()) continue;
                final long offset = UNSAFE.objectFieldOffset(field);
                UNSAFE.putObject(object, offset, null);
            }
        }
    }
    
}
