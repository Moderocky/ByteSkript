package mx.kenzie.skript.runtime.type;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;

public record Executable(Type owner, MethodErasure erasure) implements ConstantDesc, Constable {
    
    public Executable(Method method) {
        this(new Type(method.getDeclaringClass()), new MethodErasure(method));
    }
    
    @Override
    public Optional<? extends ConstantDesc> describeConstable() {
        return Optional.of(this);
    }
    
    @Override
    public Object resolveConstantDesc(MethodHandles.Lookup lookup) {
        return this;
    }
    
}
