package mx.kenzie.skript.runtime.internal;

import com.sun.management.HotSpotDiagnosticMXBean;
import mx.kenzie.skript.api.data.Serializer;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;

public class BinarySerializer extends Serializer {
    
    protected final Unsafe unsafe;
    protected final ReflectionFactory factory;
    protected final boolean compressedOops;
    
    public BinarySerializer() throws PrivilegedActionException {
        this.unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        });
        this.factory = ReflectionFactory.getReflectionFactory();
        final HotSpotDiagnosticMXBean bean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        this.compressedOops = Boolean.parseBoolean(bean.getVMOption("UseCompressedOops").getValue());
        java_16_fix:
        try {
            final Field field = Class.class.getDeclaredField("module");
            final long offset = unsafe.objectFieldOffset(field);
            this.unsafe.putObject(BinarySerializer.class, offset, Object.class.getModule());
        } catch (NoSuchFieldException ignored) {
        } // java < 11
    }
    
    @Override
    public long getAllocationSize(Object... objects) {
        long size = 0;
        for (Object object : objects) {
            size += getMemorySize(object);
        }
        return size;
    }
    
    @Override
    public void store(ByteBuffer buffer, Object... objects) {
        final long address = unsafe.allocateMemory(getAllocationSize(objects));
        long index = 0;
        for (Object object : objects) {
            long size = getMemorySize(object);
            index += 10;
        }
        
    }
    
    protected long getMemorySize(Object object) {
        final Set<Field> fields = new HashSet<>();
        Class<?> cls = object.getClass();
        while (cls != Object.class) {
            for (final Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                fields.add(field);
            }
            cls = cls.getSuperclass();
        }
        long maxSize = 0;
        for (final Field field : fields) {
            final long offset = unsafe.objectFieldOffset(field);
            if (offset > maxSize) maxSize = offset;
        }
        return ((maxSize / 8) + 1) * 8;
    }
    
    protected void assign(Object object, long address) {
        Class<?> cls = object.getClass();
        long index = address;
        do {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                long offset = unsafe.objectFieldOffset(field);
                if (field.getType() == long.class) {
                    unsafe.putLong(index += offset, unsafe.getLong(object, offset));
                } else if (field.getType() == int.class) {
                    unsafe.putInt(index += offset, unsafe.getInt(object, offset));
                } else if (field.getType() == short.class) {
                    unsafe.putShort(index += offset, unsafe.getShort(object, offset));
                } else if (field.getType() == byte.class) {
                    unsafe.putShort(index += offset, unsafe.getShort(object, offset));
                } else if (field.getType() == boolean.class) {
                    unsafe.putByte(index += offset, (byte) (unsafe.getBoolean(object, offset) ? 1 : 0));
                } else if (field.getType() == char.class) {
                    unsafe.putChar(index += offset, unsafe.getChar(object, offset));
                } else if (field.getType() == float.class) {
                    unsafe.putFloat(index += offset, unsafe.getFloat(object, offset));
                } else if (field.getType() == double.class) {
                    unsafe.putDouble(index += offset, unsafe.getDouble(object, offset));
                } else {
                    assign(unsafe.getObject(object, offset), index);
                }
            }
        } while ((cls = cls.getSuperclass()) != null);
    }
    
    
}
