package mx.kenzie.skript.api;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.note.ForceInline;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.InlineController;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.structure.PreVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public interface SyntaxElement {
    
    Pattern getPattern();
    
    default Pattern.Match match(String thing, Context context) {
        return getPattern().match(thing, context);
    }
    
    String[] getPatterns();
    
    Library getProvider();
    
    LanguageElement getType();
    
    default Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    String name();
    
    boolean hasHandler(HandlerType type);
    
    Method getHandler(HandlerType type);
    
    default boolean allowAsInputFor(Type type) {
        return type.equals(CommonTypes.OBJECT) || type.equals(getReturnType());
    }
    
    default void preCompile(Context context, Pattern.Match match) throws Throwable {
        // Very few elements require a lookahead.
    }
    
    void compile(Context context, Pattern.Match match) throws Throwable;
    
    default boolean allowedIn(State state, Context context) {
        return true;
    }
    
    class Handlers extends HashMap<HandlerType, Method> {
        
        public static final Handlers EMPTY = new Handlers();
        
    }
    
    default void writeCall(final MethodBuilder builder, final Method method, final Context context) {
        final ForceInline inline = method.getAnnotation(ForceInline.class);
        final ForceExtract extract = method.getAnnotation(ForceExtract.class);
        if (inline != null) {
            final InlineController controller = new InlineController(context);
            final Map<Integer, PreVariable> map = controller.getSpecial();
            for (int i = method.getParameterTypes().length - 1; i >= 0; i--) {
                final PreVariable var = new PreVariable("$unspec_" + i);
                context.forceUnspecVariable(var);
                map.put(i, var);
                final int slot = context.slotOf(var);
                builder.writeCode(WriteInstruction.storeObject(slot));
            }
            builder.writeCode(SourceReader.getSource(method, controller).toArray(new WriteInstruction[0]));
        } else if (extract != null) {
            final ClassBuilder parent = builder.finish();
            final MethodErasure erasure = new MethodErasure(method.getReturnType(), method.getName() + "$" + method.getDeclaringClass()
                .getSimpleName(), method.getParameterTypes());
            if (!parent.hasMatching(erasure)) {
                final MethodBuilder creator = parent.addMatching(erasure);
                creator.setModifiers(Modifier.PRIVATE | Modifier.STATIC | 0x00001000);
                creator.writeCode(SourceReader.getSource(method, null).toArray(new WriteInstruction[0]));
            }
            builder.writeCode(WriteInstruction.invokeStatic(parent.getType(), erasure));
        } else {
            if (Modifier.isStatic(method.getModifiers()))
                builder.writeCode(WriteInstruction.invokeStatic(method));
            else builder.writeCode(WriteInstruction.invokeVirtual(method));
        }
    }
    
}
