/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.error.ScriptAssertionError;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.config.ConfigMap;
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
    
    public static char unbox(Character value) {
        if (value == null) return 0;
        return value;
    }
    
    public static boolean unbox(Boolean value) {
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
        if (to instanceof ConfigMap map) ConfigMap.add(value + "", map);
        else if (to instanceof Collection collection) return collection.add(value);
        return null;
    }
    
    public static Iterator<?> acquireIterator(Object thing) {
        if (thing instanceof Iterable<?> iterable) return iterable.iterator();
        if (thing instanceof Object[] array) return Arrays.asList(array).iterator();
        if (thing instanceof Map<?, ?> map) return map.values().iterator();
        if (thing == null) return Collections.emptyIterator();
        return Collections.singletonList(thing).iterator();
    }
    
    /**
     * Moved from {@link org.byteskript.skript.lang.syntax.flow.AssertEffect}
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
     * Moved from {@link org.byteskript.skript.lang.syntax.flow.AssertWithErrorEffect}
     */
    public static void assertion(Object object, Object message, Class<?> script, int line) {
        if (object == null)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Boolean boo && !boo)
            throw new ScriptAssertionError(script, line, message + "");
        else if (object instanceof Number number && number.intValue() == 0)
            throw new ScriptAssertionError(script, line, message + "");
    }
    //endregion
    
    //region Calculations
    public static Object add(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            return "" + a + b;
        }
        if (x instanceof Integer && y instanceof Integer) return x.intValue() + y.intValue();
        if (x instanceof Float || x instanceof Double) {
            if (y instanceof Float || y instanceof Double) {
                return x.doubleValue() + y.doubleValue();
            } else {
                return x.doubleValue() + y.longValue();
            }
        } else {
            if (y instanceof Float || y instanceof Double) {
                return x.longValue() + y.doubleValue();
            } else {
                return x.longValue() + y.longValue();
            }
        }
    }
    
    public static Object subtract(Object a, Object b) {
        if (a instanceof String first && b instanceof String second) {
            if (first.endsWith(second)) return first.substring(0, first.length() - second.length());
        } else if (a instanceof String string && b instanceof Number number) {
            return string.substring(0, string.length() - number.intValue());
        }
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
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
    }
    
    public static Object multiply(Object a, Object b) {
        if (a instanceof String string && b instanceof Number number) {
            return string.repeat(Math.max(0, number.intValue()));
        } else if (b instanceof String string && a instanceof Number number) {
            return string.repeat(Math.max(0, number.intValue()));
        }
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
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
    }
    
    public static Number divide(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
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
    }
    
    public static Number root(Object a) {
        if (!(a instanceof Number x)) {
            throw new ScriptRuntimeError("Provided inputs must be numerical.");
        }
        return Math.sqrt(x.doubleValue());
    }
    //endregion
    
    public static Boolean equals(Object a, Object b) {
        if (Objects.equals(a, b)) return true;
        if (isArray(a) && isArray(b)) return Arrays.equals((Object[]) a, (Object[]) b);
        if (!(a instanceof Number x) || !(b instanceof Number y)) return false;
        return Double.compare(x.doubleValue(), y.doubleValue()) == 0;
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
    
    //region Comparisons
    public static Boolean isArray(Object a) {
        if (a == null) return false;
        return a.getClass().isArray();
    }
    //endregion
    
}
