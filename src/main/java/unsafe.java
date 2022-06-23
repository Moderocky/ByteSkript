import mx.kenzie.mirror.FieldAccessor;
import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.UnsafeAccessor;
import org.byteskript.skript.runtime.type.DataList;

import java.util.Arrays;
import java.util.Collection;

/**
 * This is the Java implementation of the 'unsafe' namespace functions.
 * Some implementations may re-implement this in Skript itself.
 * <p>
 * The unusual casing is to match Skript's code-style.
 */
public class unsafe extends UnsafeAccessor {
    
    public static DataList get_threads() {
        final DataList list = new DataList();
        final int count = get_thread_count();
        final Thread[] threads = new Thread[count];
        Skript.THREAD_GROUP.enumerate(threads);
        list.addAll(Arrays.asList(threads));
        return list;
    }
    
    public static int get_thread_count() {
        return Skript.THREAD_GROUP.activeCount();
    }
    
    public static void await_load(Class<?> type) {
        UNSAFE.ensureClassInitialized(type);
    }
    
    public static void register_converter(Class<Object> from, Class<Object> to, MethodAccessor<?> function) {
        final Skript skript = get_runtime();
        skript.registerConverter(from, to, function::invoke);
    }
    
    public static Skript get_runtime() {
        final Skript skript = Skript.localInstance();
        if (skript != null) return skript;
        return Skript.currentInstance();
    }
    
    public static void unregister_converter(Class<Object> from, Class<Object> to) {
        final Skript skript = get_runtime();
        skript.unregisterConverter(from, to);
    }
    
    public static void call_event(Event event, Class<?> script) {
        final Skript skript = get_runtime();
        if (script == null) skript.runEvent(event);
        else skript.runEvent(event, skript.getScript(script));
    }
    
    public static void sleep(Object object) throws InterruptedException {
        synchronized (object) {
            object.wait();
        }
    }
    
    public static void wake(Object object) throws InterruptedException {
        synchronized (object) {
            object.notify();
        }
    }
    
    //region Method Handles
    public static MethodAccessor<Object> get_java_method(Object owner, Object name, Object parameters) {
        final Class<?>[] arguments;
        if ((parameters) instanceof Object[] array) {
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if ((parameters) instanceof Collection<?> collection) {
            final Object[] array = collection.toArray();
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if ((parameters) == null) arguments = new Class[0];
        else {
            arguments = new Class[]{(Class<?>) parameters};
        }
        return mirror(owner).method(name + "", arguments);
    }
    
    private static Mirror<?> mirror(Object owner) {
        if (owner == null) return null;
        if (owner instanceof Class<?> type) {
            if (type.getName().startsWith("skript")) return Mirror.of(type).useProvider(Skript.findLoader());
            return Mirror.of(type);
        } else {
            if (owner.getClass().getName().startsWith("skript"))
                return Mirror.of(owner).useProvider(Skript.findLoader());
            return Mirror.of(owner);
        }
    }
    
    public static MethodAccessor<Object> get_java_method(Object owner, Object name) {
        return mirror(owner).method(name + "");
    }
    
    public static boolean has_java_field(Object owner, Object name) {
        return mirror(owner).field((name) + "") != null;
    }
    
    public static Object get_java_field(Object owner, Object name) {
        final FieldAccessor<?> accessor = mirror(owner).field((name) + "");
        if (accessor == null) return null;
        return accessor.get();
    }
    
    public static void set_java_field(Object owner, Object name, Object value) {
        final FieldAccessor<?> accessor = mirror(owner).field((name) + "");
        if (accessor != null) {
            accessor.set((value));
        } else {
            throw new IllegalArgumentException(
                    "Tried to set field " + owner.getClass().getSimpleName()
                            + "#" + name + ", but it does not exist");
        }
    }
    //endregion
    
}
