import mx.kenzie.mirror.Mirror;

import java.util.Collection;

public final class skript {
    
    public static Object getClass(Object name) throws Throwable {
        return Class.forName(name + "");
    }
    
    public static Object javaMethod(Object owner, Object name, Object parameters) {
        final Class<?>[] arguments;
        if (parameters instanceof Object[] array) {
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if (parameters instanceof Collection<?> collection) {
            final Object[] array = collection.toArray();
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if (parameters == null) arguments = new Class[0];
        else {
            arguments = new Class[] {(Class<?>) parameters};
        }
        return Mirror.of(owner).method(name + "", arguments);
    }
    
    public static Object javaMethod(Object owner, Object name) {
        return Mirror.of(owner).method(name + "");
    }
    
}
