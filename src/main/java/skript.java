import mx.kenzie.mirror.MethodAccessor;
import mx.kenzie.mirror.Mirror;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.byteskript.skript.runtime.type.AtomicVariable;

import java.util.Collection;

public final class skript {
    
    //region Maths
    
    //region Trigonometry
    public static double acos(Object object) {
        if (object == null) return 90;
        if (object instanceof Number number) return Math.toDegrees(Math.acos(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to acos(" + object + ") - not a number.");
    }
    
    public static double asin(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.asin(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to asin(" + object + ") - not a number.");
    }
    
    public static double atan(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.atan(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to atan(" + object + ") - not a number.");
    }
    
    public static double atan2(Object rawX, Object rawY) {
        double x, y;
        if (rawX == null) x = 0;
        else if (rawX instanceof Number number) x = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + rawX + ") - not a number.");
        if (rawY == null) y = 0;
        else if (rawY instanceof Number number) y = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + rawY + ") - not a number.");
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    public static double cos(Object object) {
        if (object == null) return 1;
        if (object instanceof Number number) return Math.cos(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to cos(" + object + ") - not a number.");
    }
    
    public static double cosh(Object object) {
        if (object == null) return 1;
        if (object instanceof Number number) return Math.toDegrees(Math.cosh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to cosh(" + object + ") - not a number.");
    }
    
    public static double sin(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.sin(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to sin(" + object + ") - not a number.");
    }
    
    public static double sinh(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.sinh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to sinh(" + object + ") - not a number.");
    }
    
    public static double tan(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.tan(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to tan(" + object + ") - not a number.");
    }
    
    public static double tanh(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.tanh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to tanh(" + object + ") - not a number.");
    }
    //endregion
    
    public static double to_degrees(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(number.doubleValue());
        throw new ScriptRuntimeError("Unable to to_degrees(" + object + ") - not a number.");
    }
    
    public static double to_radians(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toRadians(number.doubleValue());
        throw new ScriptRuntimeError("Unable to to_radians(" + object + ") - not a number.");
    }
    
    public static Number abs(Object object) {
        if (object == null) return 0;
        if (object instanceof Byte number) return Math.abs(number);
        if (object instanceof Short number) return Math.abs(number);
        if (object instanceof Integer number) return Math.abs(number);
        if (object instanceof Long number) return Math.abs(number);
        if (object instanceof Float number) return Math.abs(number);
        if (object instanceof Double number) return Math.abs(number);
        throw new ScriptRuntimeError("Unable to abs(" + object + ") - not a number.");
    }
    
    public static double sqrt(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.sqrt(number.doubleValue());
        throw new ScriptRuntimeError("Unable to sqrt(" + object + ") - not a number.");
    }
    
    public static double newton_root(Object object, Object accuracy) {
        if (object == null) return 0;
        final int times = (accuracy instanceof Number number) ? number.intValue() : 1;
        if (!(object instanceof Number number))
            throw new ScriptRuntimeError("Unable to sqrt(" + object + ") - not a number.");
        final double value = number.doubleValue();
        double result = Double.longBitsToDouble(((Double.doubleToLongBits(value) - (1L << 52)) >> 1) + (1L << 61));
        for (int i = 0; i < times; i++) {
            result = (result + value / result) * 0.5;
        }
        return result;
    }
    
    public static double ceil(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.ceil(number.doubleValue());
        throw new ScriptRuntimeError("Unable to ceil(" + object + ") - not a number.");
    }
    
    public static double floor(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.floor(number.doubleValue());
        throw new ScriptRuntimeError("Unable to floor(" + object + ") - not a number.");
    }
    
    public static long round(Object object) {
        if (object == null) return 0;
        if (object instanceof Float number) return Math.round(number.doubleValue());
        if (object instanceof Number number) return Math.round(number.doubleValue());
        throw new ScriptRuntimeError("Unable to round(" + object + ") - not a number.");
    }
    
    public static double ln(Object object) {
        if (object == null) return Double.NaN;
        if (object instanceof Number number) return (-Math.log(1 - number.doubleValue())) / number.doubleValue();
        throw new ScriptRuntimeError("Unable to ln(" + object + ") - not a number.");
    }
    
    public static double log(Object object) {
        if (object == null) return Math.log(0);
        if (object instanceof Number number) return Math.log(number.doubleValue());
        throw new ScriptRuntimeError("Unable to log(" + object + ") - not a number.");
    }
    //endregion
    
    //region Generic
    public static AtomicVariable get_atomic_literal(Object atomic) { // todo returning atomics
        if (atomic instanceof AtomicVariable variable) return variable;
        else return AtomicVariable.wrap(atomic);
    }
    
    public static Class<?> get_class(Object name) throws Throwable {
        return Class.forName(name + "");
    }
    
    public static long current_time_millis() {
        return System.currentTimeMillis();
    }
    
    public static String line_separator() {
        return System.lineSeparator();
    }
    
    public static long nano_time() {
        return System.nanoTime();
    }
    
    public static int hashcode(Object raw) {
        if (raw == null) return 0;
        return raw.hashCode();
    }
    
    public static boolean strict_equals(Object a, Object b) {
        return a == b;
    }
    //endregion
    
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
        return Mirror.of((owner)).method(name + "", arguments);
    }
    
    public static MethodAccessor<Object> get_java_method(Object owner, Object name) {
        return Mirror.of((owner)).method(name + "");
    }
    
    public static boolean has_java_field(Object owner, Object name) {
        return Mirror.of((owner)).field((name) + "") != null;
    }
    
    public static Object get_java_field(Object owner, Object name) {
        return Mirror.of((owner)).field((name) + "").get();
    }
    
    public static Void set_java_field(Object owner, Object name, Object value) {
        Mirror.of((owner)).field((name) + "").set((value));
        return null;
    }
    //endregion
    
    //region Thread Handlers
    public static Void clear_threadlocals() {
        if (!(Thread.currentThread() instanceof ScriptThread source)) return null;
        source.variables.clear();
        return null;
    }
    
    public static Object get_threadlocal(Object thread, Object name) {
        final String key = (name) + "";
        if (!((thread) instanceof ScriptThread target)) return null;
        return target.variables.get(key);
    }
    
    public static Void copy_threadlocals_to(Object object) {
        if (object == null) return null;
        if (!(Thread.currentThread() instanceof ScriptThread source))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!((object) instanceof ScriptThread target))
            throw new ScriptRuntimeError("Target is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
        return null;
    }
    
    public static Void copy_threadlocals_from(Object object) {
        if (object == null) return null;
        if (!(Thread.currentThread() instanceof ScriptThread target))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!((object) instanceof ScriptThread source))
            throw new ScriptRuntimeError("Source is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
        return null;
    }
    //endregion
    
}
