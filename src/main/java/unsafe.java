import mx.kenzie.mirror.MethodAccessor;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.UnsafeAccessor;
import org.byteskript.skript.runtime.type.DataList;

import java.util.Arrays;

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
    
}
