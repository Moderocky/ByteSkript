package mx.kenzie.skript.runtime.internal;

import java.lang.invoke.*;

public class Metafactory {
    
    
    public static CallSite lambda(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner) throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }
    
    
}
