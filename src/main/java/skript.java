import mx.kenzie.mirror.Mirror;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.runtime.threading.ScriptThread;
import mx.kenzie.skript.runtime.type.AtomicVariable;

import java.util.Collection;

import static mx.kenzie.skript.runtime.type.AtomicVariable.unwrap;

public final class skript {
    
    //region Maths
    
    //region Trigonometry
    public static Object acos(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 90;
        if (object instanceof Number number) return Math.toDegrees(Math.acos(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to acos(" + object + ") - not a number.");
    }
    
    public static Object asin(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.asin(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to asin(" + object + ") - not a number.");
    }
    
    public static Object atan(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.atan(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to atan(" + object + ") - not a number.");
    }
    
    public static Object atan2(Object rawX, Object rawY) {
        final Object object = unwrap(rawX);
        final Object second = unwrap(rawY);
        double x, y;
        if (object == null) x = 0;
        else if (object instanceof Number number) x = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + object + ") - not a number.");
        if (second == null) y = 0;
        else if (second instanceof Number number) y = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + second + ") - not a number.");
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    public static Object cos(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 1;
        if (object instanceof Number number) return Math.cos(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to cos(" + object + ") - not a number.");
    }
    
    public static Object cosh(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 1;
        if (object instanceof Number number) return Math.toDegrees(Math.cosh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to cosh(" + object + ") - not a number.");
    }
    
    public static Object sin(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.sin(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to sin(" + object + ") - not a number.");
    }
    
    public static Object sinh(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.sinh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to sinh(" + object + ") - not a number.");
    }
    
    public static Object tan(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.tan(Math.toRadians(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to tan(" + object + ") - not a number.");
    }
    
    public static Object tanh(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.tanh(Math.toRadians(number.doubleValue())));
        throw new ScriptRuntimeError("Unable to tanh(" + object + ") - not a number.");
    }
    //endregion
    
    public static Object to_degrees(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(number.doubleValue());
        throw new ScriptRuntimeError("Unable to to_degrees(" + object + ") - not a number.");
    }
    
    public static Object to_radians(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toRadians(number.doubleValue());
        throw new ScriptRuntimeError("Unable to to_radians(" + object + ") - not a number.");
    }
    
    public static Object abs(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Byte number) return Math.abs(number);
        if (object instanceof Short number) return Math.abs(number);
        if (object instanceof Integer number) return Math.abs(number);
        if (object instanceof Long number) return Math.abs(number);
        if (object instanceof Float number) return Math.abs(number);
        if (object instanceof Double number) return Math.abs(number);
        throw new ScriptRuntimeError("Unable to abs(" + object + ") - not a number.");
    }
    
    public static Object sqrt(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.sqrt(number.doubleValue());
        throw new ScriptRuntimeError("Unable to sqrt(" + object + ") - not a number.");
    }
    
    public static Object newton_root(Object raw, Object acc) {
        final Object object = unwrap(raw);
        final Object accuracy = unwrap(acc);
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
    
    public static Object ceil(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.ceil(number.doubleValue());
        throw new ScriptRuntimeError("Unable to ceil(" + object + ") - not a number.");
    }
    
    public static Object floor(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Number number) return Math.floor(number.doubleValue());
        throw new ScriptRuntimeError("Unable to floor(" + object + ") - not a number.");
    }
    
    public static Object round(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return 0;
        if (object instanceof Float number) return Math.round(number.doubleValue());
        if (object instanceof Number number) return Math.round(number.doubleValue());
        throw new ScriptRuntimeError("Unable to round(" + object + ") - not a number.");
    }
    
    public static Object ln(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return Double.NaN;
        if (object instanceof Number number) return (-Math.log(1 - number.doubleValue())) / number.doubleValue();
        throw new ScriptRuntimeError("Unable to ln(" + object + ") - not a number.");
    }
    
    public static Object log(Object raw) {
        final Object object = unwrap(raw);
        if (object == null) return Math.log(0);
        if (object instanceof Number number) return Math.log(number.doubleValue());
        throw new ScriptRuntimeError("Unable to log(" + object + ") - not a number.");
    }
    //endregion
    
    //region Generic
    public static Object get_atomic_literal(Object atomic) { // todo returning atomics
        if (atomic instanceof AtomicVariable) return atomic;
        else return AtomicVariable.wrap(atomic);
    }
    
    public static Object get_class(Object name) throws Throwable {
        return Class.forName(unwrap(name) + "");
    }
    
    public static Object current_time_millis() {
        return System.currentTimeMillis();
    }
    
    public static Object line_separator() {
        return System.lineSeparator();
    }
    
    public static Object nano_time() {
        return System.nanoTime();
    }
    
    public static Object hashcode(Object object) {
        final Object raw = unwrap(object);
        if (raw == null) return 0;
        return raw.hashCode();
    }
    
    public static Object strict_equals(Object a, Object b) {
        return unwrap(a) == unwrap(b);
    }
    //endregion
    
    //region Method Handles
    public static Object get_java_method(Object owner, Object name, Object parameters) {
        final Class<?>[] arguments;
        if (unwrap(parameters) instanceof Object[] array) {
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if (unwrap(parameters) instanceof Collection<?> collection) {
            final Object[] array = collection.toArray();
            arguments = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                arguments[i] = (Class<?>) array[i];
            }
        } else if (unwrap(parameters) == null) arguments = new Class[0];
        else {
            arguments = new Class[]{(Class<?>) parameters};
        }
        return Mirror.of(unwrap(owner)).method(unwrap(name) + "", arguments);
    }
    
    public static Object get_java_method(Object owner, Object name) {
        return Mirror.of(unwrap(owner)).method(unwrap(name) + "");
    }
    
    public static Object has_java_field(Object owner, Object name) {
        return Mirror.of(unwrap(owner)).field(unwrap(name) + "") != null;
    }
    
    public static Object get_java_field(Object owner, Object name) {
        return Mirror.of(unwrap(owner)).field(unwrap(name) + "").get();
    }
    
    public static Object set_java_field(Object owner, Object name, Object value) {
        Mirror.of(unwrap(owner)).field(unwrap(name) + "").set(unwrap(value));
        return null;
    }
    //endregion
    
    //region Thread Handlers
    public static Object clear_threadlocals() {
        if (!(Thread.currentThread() instanceof ScriptThread source)) return null;
        source.variables.clear();
        return null;
    }
    
    public static Object get_threadlocal(Object thread, Object name) {
        final String key = unwrap(name) + "";
        if (!(unwrap(thread) instanceof ScriptThread target)) return null;
        return target.variables.get(key);
    }
    
    public static Object copy_threadlocals_to(Object object) {
        if (object == null) return null;
        if (!(Thread.currentThread() instanceof ScriptThread source))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!(unwrap(object) instanceof ScriptThread target))
            throw new ScriptRuntimeError("Target is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
        return null;
    }
    
    public static Object copy_threadlocals_from(Object object) {
        if (object == null) return null;
        if (!(Thread.currentThread() instanceof ScriptThread target))
            throw new ScriptRuntimeError("Code is not being run on a script thread - thread variables are unavailable here.");
        if (!(unwrap(object) instanceof ScriptThread source))
            throw new ScriptRuntimeError("Source is not a script thread - thread variables are unavailable here.");
        target.variables.putAll(source.variables);
        return null;
    }
    //endregion
    
}
