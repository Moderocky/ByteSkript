package mx.kenzie.skript.api;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.compiler.Context;

import java.util.*;

public class ModifiableLibrary implements Library {
    
    protected final Map<State, List<SyntaxElement>> syntax = new HashMap<>();
    protected final List<Property> properties = new ArrayList<>();
    protected final List<Type> types = new ArrayList<>();
    protected final String name;
    
    public ModifiableLibrary(String name) {
        this.name = name;
    }
    
    public void registerSyntax(State state, SyntaxElement element) {
        this.syntax.putIfAbsent(state, new ArrayList<>());
        this.syntax.get(state).add(element);
        if (element instanceof final Property property && !properties.contains(property)) properties.add(property);
    }
    
    public Type registerType(Class<?> cls) {
        final Type type = new Type(cls);
        if (!types.contains(type)) this.types.add(type);
        return type;
    }
    
    public Type registerType(String classPath) {
        final Type type = new Type(classPath);
        if (!types.contains(type)) this.types.add(type);
        return type;
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
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
    public Collection<Property> getProperties() {
        return properties;
    }
    
    @Override
    public SyntaxElement[] getSyntax() {
        final List<SyntaxElement> elements = new ArrayList<>();
        for (List<SyntaxElement> value : syntax.values()) {
            elements.addAll(value);
        }
        return elements.toArray(new SyntaxElement[0]);
    }
    
    @Override
    public LanguageElement[] getConstructs() {
        return new LanguageElement[0];
    }
    
    @Override
    public Type[] getTypes() {
        return types.toArray(new Type[0]);
    }
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        return Collections.emptyList();
    }
}
