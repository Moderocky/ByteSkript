package mx.kenzie.skript.runtime.internal;

import mx.kenzie.skript.error.ScriptRuntimeError;

import java.util.*;

public class OperatorHandler {
    
    public static String concat(String... strings) {
        final StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }
    
    public static Object addObject(Object value, Object to) {
        if (to instanceof Collection collection) return collection.add(value);
        return null;
    }
    
    public static Iterator<?> acquireIterator(Object thing) {
        if (thing instanceof Iterable<?> iterable) return iterable.iterator();
        if (thing instanceof Object[] array) return Arrays.asList(array).iterator();
        if (thing instanceof Map<?, ?> map) return map.values().iterator();
        if (thing == null) return Collections.emptyIterator();
        return Collections.singletonList(thing).iterator();
    }
    
    //region Calculations
    public static Object add(Object a, Object b) {
        if (!(a instanceof Number x) || !(b instanceof Number y)) {
            return "" + a + b;
        }
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
    
    public static Number subtract(Object a, Object b) {
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
    
    public static Number multiply(Object a, Object b) {
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
    
    //region Comparisons
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
    
    public static Boolean equals(Object a, Object b) {
        if (Objects.equals(a, b)) return true;
        if (!(a instanceof Number x) || !(b instanceof Number y)) return false;
        return Double.compare(x.doubleValue(), y.doubleValue()) == 0;
    }
    
    public static Boolean contains(Object a, Object b) {
        if (a == null) return false;
        if (b == null) return false;
        if (a instanceof Collection<?> list) return list.contains(b);
        if (a instanceof Object[] array) return Arrays.asList(array).contains(b);
        if (a instanceof Map<?, ?> map) return map.containsKey(b) || map.containsValue(b);
        return (a + "").contains(b + "");
    }
    //endregion
    
}
