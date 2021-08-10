package mx.kenzie.skript.api;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.note.ForceBridge;
import mx.kenzie.skript.api.note.ForceExtract;
import mx.kenzie.skript.api.note.ForceInline;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.InlineController;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.structure.PreVariable;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.runtime.threading.ScriptThread;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static mx.kenzie.foundation.WriteInstruction.*;

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
    
    void setHandler(HandlerType type, Method method);
    
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
    
    default boolean isDelay() {
        return false;
    }
    
    default boolean requiresMainThread() {
        return false;
    }
    
    default void addSkipInstruction(Context context, Consumer<Context> consumer) {
        context.addSkipInstruction(consumer);
    }
    
    static void runOnMainThread(final Instruction<?> runnable) throws Throwable {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Cannot join main thread from non-script thread.");
        thread.controller.addInstruction(runnable);
        synchronized (thread.controller) {
            thread.controller.wait();
        }
    }
    
    default void writeCall(final MethodBuilder builder, final Method method, final Context context) {
        final ForceInline inline = method.getAnnotation(ForceInline.class);
        final ForceExtract extract = method.getAnnotation(ForceExtract.class);
        final ForceBridge bridge = method.getAnnotation(ForceBridge.class);
        if (bridge != null) {
            final boolean dynamic = !Modifier.isStatic(method.getModifiers());
            final List<Type> inputs = new ArrayList<>();
            if (dynamic) inputs.add(new Type(method.getDeclaringClass()));
            for (Class<?> type : method.getParameterTypes()) {
                inputs.add(new Type(type));
            }
            final MethodErasure target = new MethodErasure(new Type(method.getReturnType()), "generic_" + method.getName() + "$" + method.getDeclaringClass()
                .getSimpleName(), Type.array(CommonTypes.OBJECT, inputs.size()));
            final MethodBuilder sub;
            if (context.getBuilder().hasMatching(target)) sub = context.getBuilder().getMatching(target);
            else sub = context.getBuilder().addMatching(target);
            sub.setModifiers(0x00000002 | 0x00000008 | 0x00001000 | 0x00000040);
            int index = 0;
            for (Type input : inputs) {
                sub.writeCode(loadObject(index));
                sub.writeCode(cast(input));
                index++;
            }
            sub.writeCode(invoke(method));
            if (method.getReturnType() == void.class) sub.writeCode(returnEmpty());
            else sub.writeCode(returnObject());
            builder.writeCode(invokeStatic(context.getType(), target));
        } else if (inline != null) {
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
                creator.setModifiers(Modifier.PRIVATE | Modifier.STATIC | 0x00001000); // synthetic 0x00001000
                creator.writeCode(SourceReader.getSource(method, null).toArray(new WriteInstruction[0]));
            }
            builder.writeCode(invokeStatic(parent.getType(), erasure));
        } else {
            if (Modifier.isStatic(method.getModifiers()))
                builder.writeCode(invokeStatic(method));
            else builder.writeCode(invokeVirtual(method));
        }
    }
    
}
