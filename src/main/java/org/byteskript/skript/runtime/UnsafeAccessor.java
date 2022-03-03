/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.error.ScriptRuntimeError;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

@SuppressWarnings({"removal"})
@Ignore
public
class UnsafeAccessor {
    
    protected static final Unsafe UNSAFE;
    
    static {
        try {
            UNSAFE = java.security.AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                final Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                return (Unsafe) field.get(null);
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
    
    protected static Class<?> loadAnonymous(byte[] bytecode) {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.defineHiddenClass(bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE).lookupClass();
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeError(ex);
        }
    }
    
}
