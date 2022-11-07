/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.error.ScriptAssertionError;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.syntax.flow.EffectAssert;
import org.byteskript.skript.lang.syntax.flow.EffectAssertWithError;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.config.ConfigMap;
import org.byteskript.skript.runtime.threading.ScriptThread;
import org.byteskript.skript.runtime.type.OperatorFunction;
import org.byteskript.skript.runtime.type.Query;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Operator handler methods invoked by syntax internally.
 * <p>
 * The boxing methods are used for null=0 handling by the bridge compiler.
 */
@Ignore
public class OperatorHandler {
    
    //region Unboxing
    public static byte unboxB(Number value) {
        if (value == null) return 0;
        return value.byteValue();
    }
    
    public static short unboxS(Number value) {
        if (value == null) return 0;
        return value.shortValue();
    }
    
    public static int unboxI(Number value) {
        if (value == null) return 0;
        return value.intValue();
    }
    
    public static long unboxJ(Number value) {
        if (value == null) return 0;
        return value.longValue();
    }
    
    public static float unboxF(Number value) {
        if (value == null) return 0;
        return value.floatValue();
    }
    
    public static double unboxD(Number value) {
        if (value == null) return 0;
        return value.doubleValue();
    }
    
    public static char unboxC(Character value) {
        if (value == null) return 0;
        return value;
    }
    
    public static boolean unboxZ(Boolean value) {
        if (value == null) return false;
        return value;
    }
    //endregion
    
    //region Generic
    public static String concat(String... strings) {
        final StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }
    
    public static Object addObject(Object value, Object to) {
        if (to instanceof ConfigMap map) {
            ConfigMap.add(value + "", map);
            return map;
        } else if (to instanceof Collection collection) {
            collection.add(value);
            return collection;
        } else if (to instanceof Object[])
            throw new ScriptRuntimeError("The value '" + value + "' cannot be added to an array.\n"
                + "\t(Arrays cannot be added to - try using a list instead.)");
        else return add(to, value);
    }
    
    //region Calculations
    public static Object add(Object a, Object b) {
        if ((a instanceof Number x) && (b instanceof Number y)) {
            if (x instanceof Integer && y instanceof Integer) return x.intValue() + y.intValue();
            if (x instanceof Float || x instanceof Double) {
                if (y instanceof Float || y instanceof Double) return x.doubleValue() + y.doubleValue();
                else return x.doubleValue() + y.longValue();
            } else {
                if (y instanceof Float || y instanceof Double) return x.longValue() + y.doubleValue();
                else return x.longValue() + y.longValue();
            }
        } else {
            if (a instanceof String && b instanceof String) return "" + a + b;
            try {
                final OperatorFunction<Object, Object> function = (OperatorFunction<Object, Object>) findInstance().getOperatorFunction(OperatorFunction.Type.ADD, a.getClass(), b.getClass());
                if (function == null) return "" + a + b;
                return function.union(a, b);
            } catch (Throwable e) {
                if (e instanceof ScriptRuntimeError error) throw error;
                throw new ScriptRuntimeError(e);
            }
        }
    }
    
    private static Skript findInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) return Skript.currentInstance();
        return thread.skript;
    }
    
    public static Object removeObject(Object value, Object to) {
        if (to instanceof Map map) {
            map.remove(value);
            return map;
        } else if (to instanceof Collection collection) {
            collection.remove(value);
            return collection;
        } else if (to instanceof Object[] array)
            throw new ScriptRuntimeError("The value '" + value + "' cannot be added to an array.\n"
                + "\t(Arrays cannot be added to - try using a list instead.)");
        else return subtract(to, value);
    }
    
    public static Object subtract(Object a, Object b) {
        if (a instanceof String first && b instanceof String second) {
            if (first.endsWith(second)) return first.substring(0, first.length() - second.length());
        } else if (a instanceof String string && b instanceof Number number) {
            return string.substring(0, string.length() - number.intValue());
        }
        if ((a instanceof Number x) && (b instanceof Number y)) {
            if (x instanceof Float || x instanceof Double) {
                if (y instanceof Float || y instanceof Double) {
                    return x.doubleValue() - y.doubleValue();
                } else {
                    return x.doubleValue() - y.longValue();
                }
            } else {
                if (y instanceof Float || y instanceof Double) {
                    return x.longValue() - y.doubleValue();
                } else {
                    return x.longValue() - y.longValue();
                }
            }
        } else {
            try {
                final OperatorFunction<Object, Object> function = (OperatorFunction<Object, Object>) findInstance().getOperatorFunction(OperatorFunction.Type.SUB, a.getClass(), b.getClass());
                if (function == null) throw new ScriptRuntimeError("Unable to subtract '" + b + "' from '" + a + "'.");
                return function.union(a, b);
            } catch (Throwable e) {
                if (e instanceof ScriptRuntimeError error) throw error;
                throw new ScriptRuntimeError(e);
            }
        }
    }
    //endregion
    
    public static Iterator<?> acquireIterator(Object thing) {
        if (thing instanceof Iterable<?> iterable) return iterable.iterator();
        if (thing instanceof Object[] array) return Arrays.asList(array).iterator();
        if (thing instanceof Map<?, ?> map) return map.values().iterator();
        if (thing == null) return Collections.emptyIterator();
        return Collections.singletonList(thing).iterator();
    }
    
    /**
     * Moved from {@link EffectAssert}
     */
    public static void assertion(Object object, Class<?> script, int line) {
        if (object == null)
            throw new ScriptAssertionError(script, line);
        else if (object instanceof Boolean boo && !boo)
            throw new ScriptAssertionError(script, line);
        else if (object instanceof Number number && number.intValue() == 0)
            throw new ScriptAssertionError(script, line);
    }
    
    /**
     * Moved from {@link EffectAssertWithError}
     */
    public static void assertion(Object object, Object message, Class<?> script, int line) {
        if (object == null)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Boolean boo && !boo)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Number number && number.intValue() == 0)
            throw new ScriptAssertionError(script, line, message + "");
    }
    
    public static Object multiply(Object a, Object b) {
        if (a instanceof String string && b instanceof Number number) {
            return string.repeat(Math.max(0, number.intValue()));
        } else if (b instanceof String string && a instanceof Number number) {
            return string.repeat(Math.max(0, number.intValue()));
        }
        if ((a instanceof Number x) && (b instanceof Number y)) {
            if (x instanceof Float || x instanceof Double) {
                if (y instanceof Float || y instanceof Double) {
                    return x.doubleValue() * y.doubleValue();
                } else {
                    return x.doubleValue() * y.longValue();
                }
            } else {
                if (y instanceof Float || y instanceof Double) {
                    return x.longValue() * y.doubleValue();
                } else {
                    return x.longValue() * y.longValue();
                }
            }
        } else {
            try {
                final OperatorFunction<Object, Object> function = (OperatorFunction<Object, Object>) findInstance().getOperatorFunction(OperatorFunction.Type.MUL, a.getClass(), b.getClass());
                if (function == null) throw new ScriptRuntimeError("Unable to multiply '" + a + "' with '" + b + "'.");
                return function.union(a, b);
            } catch (Throwable e) {
                if (e instanceof ScriptRuntimeError error) throw error;
                throw new ScriptRuntimeError(e);
            }
        }
    }
    
    public static Number divide(Object a, Object b) {
        if ((a instanceof Number x) && (b instanceof Number y)) {
            if (x instanceof Float || x instanceof Double) {
                if (y instanceof Float || y instanceof Double) {
                    return x.doubleValue() / y.doubleValue();
                } else {
                    return x.doubleValue() / y.longValue();
                }
            } else {
                if (y instanceof Float || y instanceof Double) {
                    return x.longValue() / y.doubleValue();
                } else {
                    return x.longValue() / y.longValue();
                }
            }
        } else {
            try {
                final OperatorFunction<Object, Object> function = (OperatorFunction<Object, Object>) findInstance().getOperatorFunction(OperatorFunction.Type.DIV, a.getClass(), b.getClass());
                if (function == null) throw new ScriptRuntimeError("Unable to divide '" + a + "' by '" + b + "'.");
                return (Number) function.union(a, b);
            } catch (Throwable e) {
                if (e instanceof ScriptRuntimeError error) throw error;
                throw new ScriptRuntimeError(e);
            }
        }
    }
    //endregion
    
    public static Number root(Object a) {
        if (!(a instanceof Number x)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        return Math.sqrt(x.doubleValue());
    }
    
    public static Boolean gt(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        if (x instanceof Float || x instanceof Double) {
            if (y instanceof Float || y instanceof Double) {
                return x.doubleValue() > y.doubleValue();
            } else {
                return x.doubleValue() > y.longValue();
            }
        } else {
            if (y instanceof Float || y instanceof Double) {
                return x.longValue() > y.doubleValue();
            } else {
                return x.longValue() > y.longValue();
            }
        }
    }
    
    public static Boolean gteq(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        if (x instanceof Float || x instanceof Double) {
            if (y instanceof Float || y instanceof Double) {
                return x.doubleValue() >= y.doubleValue();
            } else {
                return x.doubleValue() >= y.longValue();
            }
        } else {
            if (y instanceof Float || y instanceof Double) {
                return x.longValue() >= y.doubleValue();
            } else {
                return x.longValue() >= y.longValue();
            }
        }
    }
    
    public static Boolean lt(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        if (x instanceof Float || x instanceof Double) {
            if (y instanceof Float || y instanceof Double) {
                return x.doubleValue() < y.doubleValue();
            } else {
                return x.doubleValue() < y.longValue();
            }
        } else {
            if (y instanceof Float || y instanceof Double) {
                return x.longValue() < y.doubleValue();
            } else {
                return x.longValue() < y.longValue();
            }
        }
    }
    
    public static Boolean lteq(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        if (x instanceof Float || x instanceof Double) {
            if (y instanceof Float || y instanceof Double) {
                return x.doubleValue() <= y.doubleValue();
            } else {
                return x.doubleValue() <= y.longValue();
            }
        } else {
            if (y instanceof Float || y instanceof Double) {
                return x.longValue() <= y.doubleValue();
            } else {
                return x.longValue() <= y.longValue();
            }
        }
    }
    
    public static Boolean contains(Object a, Object b) {
        if (a == null) return false;
        if (b == null) return false;
        if (a instanceof Collection<?> list) return list.contains(b);
        if (a instanceof Object[] array) return Arrays.asList(array).contains(b);
        if (a instanceof Map<?, ?> map) return map.containsKey(b) || map.containsValue(b);
        return (a + "").contains(b + "");
    }
    
    public static Boolean matches(Object a, Object b) {
        if (a == null) return false;
        if (b == null) return false;
        if (b instanceof Query query) return query.check(a);
        if (b instanceof Pattern pattern) return pattern.matcher(a + "").matches();
        return equals(a, b); // probably a mistaken use?
    }
    
    public static Boolean equals(Object a, Object b) {
        if (Objects.equals(a, b)) return true;
        if (isArray(a) && isArray(b)) return Arrays.equals((Object[]) a, (Object[]) b);
        if (!(a instanceof Number x) || !(b instanceof Number y)) return false;
        return Double.compare(x.doubleValue(), y.doubleValue()) == 0;
    }
    //endregion
    
    //region Comparisons
    public static Boolean isArray(Object a) {
        if (a == null) return false;
        return a.getClass().isArray();
    }
    
}
