package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.runtime.data.HandlerData;
import org.objectweb.asm.Label;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PropertyAccessGenerator {
    
    private final HandlerType type;
    private final String name;
    private final Map<Type, Method> handlers;
    
    public PropertyAccessGenerator(HandlerType type, String name) {
        this.type = type;
        this.name = name;
        this.handlers = new HashMap<>();
    }
    
    public HandlerType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public Map<Type, Method> getHandlers() {
        return handlers;
    }
    
    public void addUse(Type type, Method method) {
        this.handlers.putIfAbsent(type, method);
    }
    
    public void compile(Context context) {
        final MethodBuilder method = context.getBuilder().addMethod("property_" + type.name() + "$" + name);
        method.setModifiers(Modifier.PRIVATE | Modifier.STATIC);
        if (this.type.expectReturn()) method.setReturnType(CommonTypes.OBJECT);
        else method.setReturnType(new Type(void.class));
        method.addParameter(CommonTypes.OBJECT); // holder
        if (this.type.expectInputs()) method.addParameter(CommonTypes.OBJECT); // change value
        method.addAnnotation(HandlerData.class).setVisible(true)
            .addValue("type", type.name())
            .addValue("name", name);
        for (Map.Entry<Type, Method> entry : handlers.entrySet()) {
            final Type type = entry.getKey();
            final Method target = entry.getValue();
            final Label jump = new Label();
            final int index = Modifier.isStatic(target.getModifiers()) ? 1 : 0;
            final Type holder = index > 0
                ? new Type(target.getParameterTypes()[0])
                : new Type(target.getDeclaringClass());
            final Type cast = this.type.expectInputs()
                ? new Type(target.getParameterTypes()[index])
                : CommonTypes.OBJECT;
            method.writeCode((writer, visitor) -> {
                visitor.visitVarInsn(25, 0); // load holder
                visitor.visitTypeInsn(193, type.internalName()); // instance of type
                visitor.visitJumpInsn(153, jump); // goto end if false
                visitor.visitVarInsn(25, 0); // load holder
                visitor.visitTypeInsn(192, holder.internalName()); // cast to what the handler needs
            });
            if (this.type.expectInputs()) {
                method.writeCode((writer, visitor) -> visitor.visitVarInsn(25, 1)); // load change value
                method.writeCode(WriteInstruction.cast(cast)); // cast to what the handler needs
            }
            method.writeCode(WriteInstruction.invoke(target)); // invoke handler
            if (this.type.expectReturn()) method.writeCode(WriteInstruction.returnObject()); // return value
            else method.writeCode(WriteInstruction.returnEmpty()); // return empty
            method.writeCode((writer, visitor) -> visitor.visitLabel(jump)); // end
        }
        if (this.type.expectReturn()) { // return null if requires result
            method.writeCode(WriteInstruction.pushNull());
            method.writeCode(WriteInstruction.returnObject());
        } else method.writeCode(WriteInstruction.returnEmpty()); // return top
    }
}
