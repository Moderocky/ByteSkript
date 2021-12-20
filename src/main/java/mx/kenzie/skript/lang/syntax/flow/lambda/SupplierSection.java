package mx.kenzie.skript.lang.syntax.flow.lambda;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.ExtractedSection;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class SupplierSection extends ExtractedSection {
    
    public SupplierSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a ]new supplier");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.SUPPLIER;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.SUPPLIER.equals(type) || CommonTypes.EXECUTABLE.equals(type);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        if (!context.isSectionHeader())
            throw new ScriptCompileError(context.lineNumber(), "Supplier has no body section.");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        super.compile(context, match);
        context.addInnerClass(Type.of("java/lang/invoke/MethodHandles$Lookup"), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
        final MethodBuilder method = context.getMethod();
        final int index = context.getLambdaIndex();
        final int load = context.getVariableCount();
        context.increaseLambdaIndex();
        final String internal = context.getType().internalName();
        final String name = "lambda$L" + index;
        final MethodBuilder child = context.getBuilder().addMethod(name)
            .setModifiers(Modifier.PUBLIC | Modifier.STATIC | 0x00001000)
            .setReturnType(CommonTypes.OBJECT);
        for (int i = 0; i < load; i++) {
            child.addParameter(CommonTypes.OBJECT);
            method.writeCode(WriteInstruction.loadObject(i));
        }
        final MethodErasure target = child.getErasure();
        final MethodErasure creator = new MethodErasure(CommonTypes.SUPPLIER, "get", child.getErasure()
            .parameterTypes());
        final MethodErasure bootstrap = new MethodErasure(LambdaMetafactory.class.getMethod("metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class));
        this.addSkipInstruction(context, c -> c.setMethod(child));
        method.writeCode((writer, visitor) -> visitor.visitInvokeDynamicInsn("get", creator.getDescriptor(), new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", bootstrap.name(), bootstrap.getDescriptor(), false), org.objectweb.asm.Type.getType("()Ljava/lang/Object;"), new Handle(6, internal, target.name(), target.getDescriptor(), false), org.objectweb.asm.Type.getType("()Ljava/lang/Object;")));
    }
    
    
}
