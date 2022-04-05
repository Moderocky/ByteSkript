/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.syntax.EventHolder;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.OperatorFunction;

import java.lang.reflect.Method;
import java.util.*;

@Description("""
    A modifiable syntax library.
    
    This is the most basic [Library](Library.html) implementation,
    and the one recommended for most providers to use.
    
    The built-in Skript language specification extends this.
    
    Syntax instances must be registered to the library before it is given to the compiler.
    """)
public class ModifiableLibrary implements SyntaxAnnotationUnwrapper, Library {
    
    @Description("A state/syntax map for filtering syntax.")
    protected final Map<State, List<SyntaxElement>> syntax = new HashMap<>();
    @Description("A list of registered properties.")
    protected final List<PropertyHandler> properties = new ArrayList<>();
    @Description("A list of registered types.")
    protected final List<Type> types = new ArrayList<>();
    @Description("This library's name for debugging and error messages.")
    protected final String name;
    @Ignore
    protected final Map<Converter.Data, Converter<?, ?>> converters = new HashMap<>();
    @Ignore
    protected final Map<OperatorFunction.Data, OperatorFunction<?, ?>> operators = new HashMap<>();
    
    @Description("""
        Create a new library instance with the given name.
        
        This is designed to be extended/overridden.
        """)
    public ModifiableLibrary(String name) {
        this.name = name;
    }
    
    @Description("""
        Registers syntax elements for a given compile-state.
        """)
    public void registerSyntax(State state, SyntaxElement... elements) {
        for (SyntaxElement element : elements) {
            this.registerSyntax(state, element);
        }
    }
    
    @Description("""
        Registers event holders.
        """)
    public void registerEvents(EventHolder... events) {
        for (EventHolder event : events) {
            this.registerEvent(event);
        }
    }
    
    @Description("""
        Registers special property expressions.
        """)
    public void registerProperty(PropertyHandler handler) {
        this.properties.add(handler);
    }
    
    @Description("""
        Registers types to this library.
        """)
    public void registerTypes(Class<?>... classes) {
        for (Class<?> aClass : classes) {
            this.registerType(aClass);
        }
    }
    
    @Ignore
    public Type registerType(Class<?> cls) {
        final Type type = new Type(cls);
        if (!types.contains(type)) this.types.add(type);
        return type;
    }
    
    @Ignore
    public void registerEvent(EventHolder event) {
        this.registerSyntax(CompileState.ROOT, event);
        this.registerValues(event);
    }
    
    @Ignore
    public void registerSyntax(State state, SyntaxElement element) {
        this.syntax.putIfAbsent(state, new ArrayList<>());
        this.syntax.get(state).add(element);
    }
    
    @Ignore
    public void registerProperty(String name, HandlerType type, Method handler) {
        this.properties.add(new PropertyHandler(type, handler, name));
    }
    
    @Description("""
        Registers types to this library.
        
        This version accepts Foundation types.
        """)
    public void registerTypes(Type... types) {
        for (final Type type : types) {
            if (!this.types.contains(type)) this.types.add(type);
        }
    }
    
    public <From, To> void registerConverter(Class<From> from, Class<To> to, Converter<From, To> converter) {
        final Converter.Data data = new Converter.Data(from, to);
        this.converters.put(data, converter);
    }
    
    public <First, Second> void registerOperator(OperatorFunction.Type type, Class<First> first, Class<Second> second, OperatorFunction<First, Second> function) {
        final OperatorFunction.Data data = new OperatorFunction.Data(type, first, second);
        this.operators.put(data, function);
    }
    
    @Ignore
    public Type registerType(String classPath) {
        final Type type = new Type(classPath);
        if (!types.contains(type)) this.types.add(type);
        return type;
    }
    
    @Override
    @Ignore
    public String name() {
        return name;
    }
    
    @Override
    @Ignore
    public Collection<SyntaxElement> getHandlers(State state, LanguageElement expected, Context context) {
        if (!syntax.containsKey(state)) return Collections.emptyList();
        final List<SyntaxElement> list = new ArrayList<>(syntax.get(state));
        list.removeIf(element -> {
            if (expected != null && element.getType() != expected) return true;
            return !element.allowedIn(state, context);
        });
        return list;
    }
    
    @Override
    @Ignore
    public Collection<PropertyHandler> getProperties() {
        return properties;
    }
    
    @Override
    @Ignore
    public LanguageElement[] getConstructs() {
        return new LanguageElement[0];
    }
    
    @Override
    @Ignore
    public Type[] getTypes() {
        return types.toArray(new Type[0]);
    }
    
    @Override
    @Ignore
    public Collection<PostCompileClass> getRuntime() {
        return Collections.emptyList();
    }
    
    @Override
    @Ignore
    public SyntaxElement[] getSyntax() {
        final List<SyntaxElement> elements = new ArrayList<>();
        for (List<SyntaxElement> value : syntax.values()) {
            elements.addAll(value);
        }
        return elements.toArray(new SyntaxElement[0]);
    }
    
    @Override
    @Ignore
    public Map<Converter.Data, Converter<?, ?>> getConverters() {
        return converters;
    }
    
    @Override
    public Map<OperatorFunction.Data, OperatorFunction<?, ?>> getOperators() {
        return operators;
    }
}
