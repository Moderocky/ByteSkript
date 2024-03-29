import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.byteskript.skript.runtime.type.AtomicVariable;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * This is the Java implementation of the 'skript' namespace functions.
 * Some implementations may re-implement this in Skript itself.
 * <p>
 * The unusual casing is to match Skript's code-style.
 */
public final class skript {
    
    //region Maths
    
    //region Trigonometry
    public static double acos(double number) {
        if (number == 0) return 90;
        return Math.toDegrees(Math.acos(number));
    }
    
    public static double asin(double number) {
        if (number == 0) return 0;
        return Math.toDegrees(Math.asin(number));
    }
    
    public static double atan(double number) {
        if (number == 0) return 0;
        return Math.toDegrees(Math.atan(number));
    }
    
    public static double atan2(double x, double y) {
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    public static double cos(double number) {
        if (number == 0) return 1;
        return Math.cos(Math.toRadians(number));
    }
    
    public static double cosh(double number) {
        if (number == 0) return 1;
        return Math.toDegrees(Math.cosh(Math.toRadians(number)));
    }
    
    public static double sin(double number) {
        if (number == 0) return 0;
        return Math.sin(Math.toRadians(number));
    }
    
    public static double sinh(double number) {
        if (number == 0) return 0;
        return Math.toDegrees(Math.sinh(Math.toRadians(number)));
    }
    
    public static double tan(double number) {
        if (number == 0) return 0;
        return Math.tan(Math.toRadians(number));
    }
    
    public static double tanh(double number) {
        if (number == 0) return 0;
        return Math.toDegrees(Math.tanh(Math.toRadians(number)));
    }
    //endregion
    
    public static double to_degrees(double number) {
        if (number == 0) return 0;
        return Math.toDegrees(number);
    }
    
    public static double to_radians(double number) {
        if (number == 0) return 0;
        return Math.toRadians(number);
    }
    
    public static Number abs(Number object) { // the instance-checks make sure the correct absolute is used
        if (object == null) return 0;
        if (object instanceof Byte number) return Math.abs(number);
        if (object instanceof Short number) return Math.abs(number);
        if (object instanceof Integer number) return Math.abs(number);
        if (object instanceof Long number) return Math.abs(number);
        if (object instanceof Float number) return Math.abs(number);
        if (object instanceof Double number) return Math.abs(number);
        throw new ScriptRuntimeError("Unable to abs(" + object + ") - not a number.");
    }
    
    public static double sum(Number... numbers) {
        double value = 0;
        for (Number number : numbers) value += number.doubleValue();
        return value;
    }
    
    public static double sqrt(double number) {
        if (number == 0) return 0;
        return Math.sqrt(number);
    }
    
    public static double newton_root(Object object, Object accuracy) {
        if (object == null) return 0;
        final int times = (accuracy instanceof Number number) ? number.intValue() : 1;
        if (!(object instanceof Number number))
            throw new ScriptRuntimeError("Unable to root(" + object + ") - not a number.");
        final double value = number.doubleValue();
        double result = Double.longBitsToDouble(((Double.doubleToLongBits(value) - (1L << 52)) >> 1) + (1L << 61));
        for (int i = 0; i < times; i++) result = (result + value / result) * 0.5;
        return result;
    }
    
    public static double ceil(double number) {
        if (number == 0) return 0;
        return Math.ceil(number);
    }
    
    public static double floor(double number) {
        if (number == 0) return 0;
        return Math.floor(number);
    }
    
    public static int round(double number) {
        if (number == 0) return 0;
        return (int) Math.round(number);
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
    public static WeakReference<?> weak_reference(Object value) {
        if (value instanceof WeakReference<?> reference) return reference;
        if (value instanceof Reference<?> reference) return new WeakReference<>(reference.get());
        return new WeakReference<>(value);
    }
    
    public static SoftReference<?> soft_reference(Object value) {
        if (value instanceof SoftReference<?> reference) return reference;
        if (value instanceof Reference<?> reference) return new SoftReference<>(reference.get());
        return new SoftReference<>(value);
    }
    
    public static Object reference_value(Object object) {
        if (object instanceof Reference<?> reference) return reference.get();
        return object;
    }
    
    public static AtomicVariable get_atomic_literal(Object atomic) {
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
    
    //region Thread Handlers
    public static void clear_threadlocals() {
        if (!(Thread.currentThread() instanceof ScriptThread source)) return;
        source.variables.clear();
    }
    
    public static Object get_threadlocal(Object thread, Object name) {
        final String key = (name) + "";
        if (!((thread) instanceof ScriptThread target)) return null;
        return target.variables.get(key);
    }
    
    public static void copy_threadlocals_to(Object object) {
        if (object == null) return;
        if (!(Thread.currentThread() instanceof ScriptThread source))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!((object) instanceof ScriptThread target))
            throw new ScriptRuntimeError("Target is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
    }
    
    public static void copy_threadlocals_from(Object object) {
        if (object == null) return;
        if (!(Thread.currentThread() instanceof ScriptThread target))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!((object) instanceof ScriptThread source))
            throw new ScriptRuntimeError("Source is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
    }
    //endregion
    
}
