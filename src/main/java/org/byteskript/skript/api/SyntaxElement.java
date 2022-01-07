/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceBridge;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.note.ForceInline;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptReassemblyError;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static mx.kenzie.foundation.WriteInstruction.*;

/**
 * A syntax element.
 * This instance controls the matching and compiling for a specific element.
 * The library it comes from controls where it can be matched.
 */
public interface SyntaxElement {
    
    /**
     * Attempts to match this element to a source string.
     * The context can be accessed, but should not be modified here.
     * If the source does not match this element, return `null`.
     * If the source does match this element, return a match object with the match data in.
     * <p>
     * Complex patterns are advised to override this and attempt faster
     * checks with {@link String#contains(CharSequence)} before running the super matcher.
     * This will circumvent a complex regex having to be checked every time.
     *
     * @param thing   the source
     * @param context the context of this source
     * @return the match result
     */
    default Pattern.Match match(String thing, Context context) {
        return this.getPattern().match(thing, context);
    }
    
    /**
     * The pattern metadata object.
     *
     * @return the pattern
     */
    Pattern getPattern();
    
    /**
     * The library that provides this syntax element.
     *
     * @return the provider
     */
    Library getProvider();
    
    /**
     * For basic syntax, this can return a method that will be used as a handle.
     *
     * @param type the handler mode
     * @return the method handle
     */
    Method getHandler(HandlerType type);
    
    void setHandler(HandlerType type, Method method);
    
    default CompileState getSubState() {
        return CompileState.STATEMENT;
    }
    
    default void prepareExpectedTypes(Context context, Method target) {
        if (target == null) return;
        final ElementTree[] inputs = context.getCompileCurrent().nested();
        if (inputs.length == 0) return;
        final Type[] types = Type.of(target.getParameterTypes());
        for (int i = 0; i < Math.min(types.length, inputs.length); i++) {
            inputs[i].wanted = types[i];
        }
    }
    
    default boolean allowAsInputFor(Type type) {
        if (CommonTypes.REFERENT.equals(type) && hasHandler(StandardHandlers.SET)) return true;
        return type.equals(CommonTypes.OBJECT) || type.equals(getReturnType());
    }
    
    boolean hasHandler(HandlerType type);
    
    /**
     * The return type of this element, used for type tracking during compilation.
     *
     * @return the return type
     */
    default Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    /**
     * This is called during the first compiler pass, Out->In, L->R.
     * Simple elements will not need this, but lookahead or re-ordering operations can be run here.
     *
     * @param context the compiler context
     * @param match   the match used
     * @throws Throwable to avoid unnecessary try/catch blocks
     */
    default void preCompile(Context context, Pattern.Match match) throws Throwable {
        // Very few elements require a lookahead.
    }
    
    /**
     * This is called during the second compiler pass, In->Out, L->R.
     * Non-trivial syntax may need to override this to provide special behaviour.
     *
     * @param context the compiler context
     * @param match   the match used
     * @throws Throwable to avoid unnecessary try/catch blocks
     */
    void compile(Context context, Pattern.Match match) throws Throwable;
    
    default boolean allowedIn(State state, Context context) {
        return true;
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
    
    /**
     * This writes a smart method call for the syntax, obeying rewrite rules.
     * This can perform inlining, extraction and basic rewrites.
     * This also has access to the bridge compiler.
     *
     * @param builder the method builder to use
     * @param method  the target method
     * @param context the compiler context
     */
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
            final InlineController controller = new InlineController(context, method);
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
    
    /**
     * A wrapped version of {@link Class#getMethod(String, Class[])} to avoid catching exceptions.
     */
    default Method findMethod(Class<?> owner, String name, Class<?>... parameters) {
        try {
            return owner.getMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    /**
     * For script reassembly - taking the post-parse structure and generating a (new) text script from it.
     *
     * @param inputs the (probably-expression) inputs that go into this line
     * @return the string that would parse as this syntax, containing the inputs
     * @throws ScriptReassemblyError if you do not wish to support this / the inputs are wrong
     */
    default String assemble(int line, String... inputs) throws ScriptReassemblyError {
        throw new ScriptReassemblyError(line, "Not supported yet.");
    }
    
    default Document createDocument() {
        return new Document(name(), getType().name(), getPatterns(), description(), examples());
    }
    
    default String name() {
        final Documentation documentation = this.getClass().getAnnotation(Documentation.class);
        if (documentation == null) return getPattern().name();
        return documentation.name();
    }
    
    LanguageElement getType();
    
    String[] getPatterns();
    
    default String description() {
        final Documentation documentation = this.getClass().getAnnotation(Documentation.class);
        if (documentation == null) return "No description.";
        return documentation.description();
    }
    
    default String[] examples() {
        final Documentation documentation = this.getClass().getAnnotation(Documentation.class);
        if (documentation == null) return new String[0];
        return documentation.examples();
    }
    
    class Handlers extends HashMap<HandlerType, Method> {
    
    }
    
}
