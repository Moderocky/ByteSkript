import mx.kenzie.mirror.Mirror;
import mx.kenzie.skript.error.ScriptRuntimeError;

import java.util.Collection;

public final class skript {
    
    //region Maths
    
    //region Trigonometry
    public static Object acos(Object object) {
        if (object == null) return 90;
        if (object instanceof Number number) return Math.toDegrees(Math.acos(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to acos(" + object + ") - not a number.");
    }
    
    public static Object asin(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.asin(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to asin(" + object + ") - not a number.");
    }
    
    public static Object atan(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.atan(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to atan(" + object + ") - not a number.");
    }
    
    public static Object atan2(Object object, Object second) {
        double x, y;
        if (object == null) x = 0;
        else if (object instanceof Number number) x = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + object + ") - not a number.");
        if (second == null) y = 0;
        else if (object instanceof Number number) y = number.doubleValue();
        else throw new ScriptRuntimeError("Unable to atan2(" + object + ") - not a number.");
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    public static Object cos(Object object) {
        if (object == null) return 1;
        if (object instanceof Number number) return Math.toDegrees(Math.cos(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to cos(" + object + ") - not a number.");
    }
    
    public static Object cosh(Object object) {
        if (object == null) return 1;
        if (object instanceof Number number) return Math.toDegrees(Math.cosh(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to cosh(" + object + ") - not a number.");
    }
    
    public static Object sin(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.sin(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to sin(" + object + ") - not a number.");
    }
    
    public static Object sinh(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.sinh(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to sinh(" + object + ") - not a number.");
    }
    
    public static Object tan(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.tan(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to tan(" + object + ") - not a number.");
    }
    
    public static Object tanh(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.toDegrees(Math.tanh(number.doubleValue()));
        throw new ScriptRuntimeError("Unable to tanh(" + object + ") - not a number.");
    }
    //endregion
    
    public static Object abs(Object object) {
        if (object == null) return 0;
        if (object instanceof Byte number) return Math.abs(number);
        if (object instanceof Short number) return Math.abs(number);
        if (object instanceof Integer number) return Math.abs(number);
        if (object instanceof Long number) return Math.abs(number);
        if (object instanceof Float number) return Math.abs(number);
        if (object instanceof Double number) return Math.abs(number);
        throw new ScriptRuntimeError("Unable to abs(" + object + ") - not a number.");
    }
    
    public static Object sqrt(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.sqrt(number.doubleValue());
        throw new ScriptRuntimeError("Unable to sqrt(" + object + ") - not a number.");
    }
    
    public static Object newtonRoot(Object object, Object accuracy) {
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
    
    public static Object ceil(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.ceil(number.doubleValue());
        throw new ScriptRuntimeError("Unable to ceil(" + object + ") - not a number.");
    }
    
    public static Object floor(Object object) {
        if (object == null) return 0;
        if (object instanceof Number number) return Math.floor(number.doubleValue());
        throw new ScriptRuntimeError("Unable to floor(" + object + ") - not a number.");
    }
    
    public static Object round(Object object) {
        if (object == null) return 0;
        if (object instanceof Float number) return Math.round(number.doubleValue());
        if (object instanceof Number number) return Math.round(number.doubleValue());
        throw new ScriptRuntimeError("Unable to round(" + object + ") - not a number.");
    }
    
    public static Object ln(Object object) {
        if (object == null) return Double.NaN;
        if (object instanceof Number number) return (-Math.log(1 - number.doubleValue())) / number.doubleValue();
        throw new ScriptRuntimeError("Unable to ln(" + object + ") - not a number.");
    }
    
    public static Object log(Object object) {
        if (object == null) return Math.log(0);
        if (object instanceof Number number) return Math.log(number.doubleValue());
        throw new ScriptRuntimeError("Unable to log(" + object + ") - not a number.");
    }
    //endregion
    
    //region System
    public static Object getClass(Object name) throws Throwable {
        return Class.forName(name + "");
    }
    
    public static Object currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public static Object lineSeparator() {
        return System.lineSeparator();
    }
    
    public static Object nanoTime() {
        return System.nanoTime();
    }
    
    public static Object hashcode(Object object) {
        if (object == null) return 0;
        return object.hashCode();
    }
    //endregion
    
    //region Method Handles
    public static Object getJavaMethod(Object owner, Object name, Object parameters) {
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
            arguments = new Class[]{(Class<?>) parameters};
        }
        return Mirror.of(owner).method(name + "", arguments);
    }
    
    public static Object getJavaMethod(Object owner, Object name) {
        return Mirror.of(owner).method(name + "");
    }
    
    public static Object hasJavaField(Object owner, Object name) {
        return Mirror.of(owner).field(name + "") != null;
    }
    
    public static Object getJavaField(Object owner, Object name) {
        return Mirror.of(owner).field(name + "").get();
    }
    
    public static Object setJavaField(Object owner, Object name, Object value) {
        Mirror.of(owner).field(name + "").set(value);
        return null;
    }
    //endregion
    
}
