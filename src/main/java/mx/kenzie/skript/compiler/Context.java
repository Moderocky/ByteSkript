package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.FieldBuilder;
import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.structure.Function;
import mx.kenzie.skript.compiler.structure.PreVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Context {
    
    protected final List<Library> libraries = new ArrayList<>();
    protected final List<Unit> units = new ArrayList<>();
    protected final List<SectionMeta> sections = new ArrayList<>();
    protected State state;
    
    public String getStoredVariableName() {
        return storedVariableName;
    }
    
    public void setStoredVariableName(String storedVariableName) {
        this.storedVariableName = storedVariableName;
    }
    
    protected String storedVariableName;
    
    public abstract LanguageElement getExpected();
    
    public abstract Collection<Type> getAvailableTypes();
    
    public abstract Type getType(String name);
    
    public abstract void registerType(String name, Type type);
    
    public abstract int indent();
    
    public abstract String indentUnit();
    
    public abstract void setIndentUnit(String string);
    
    public abstract ClassBuilder getBuilder();
    
    public abstract ClassBuilder addSuppressedBuilder(final Type type);
    
    public abstract MethodBuilder getMethod();
    
    public abstract FieldBuilder getField();
    
    public abstract void setMethod(MethodBuilder method);
    
    public abstract void setField(FieldBuilder field);
    
    public abstract int lineNumber();
    
    public abstract SyntaxElement currentEffect();
    
    public abstract PreVariable forceUnspecVariable(PreVariable variable);
    
    public abstract PreVariable getVariable(String name);
    
    public abstract PreVariable getVariable(int slot);
    
    public abstract int slotOf(PreVariable variable);
    
    public abstract boolean hasVariable(String name);
    
    public abstract ElementTree getLine();
    
    public void addSection(Section handler) {
        sections.add(0, new SectionMeta(handler));
    }
    
    public SectionMeta getSection() {
        if (sections.isEmpty()) return null;
        return sections.get(0);
    }
    
    public Section getParent() {
        if (sections.isEmpty()) return null;
        return sections.get(0).handler();
    }
    
    public void destroySection() {
        if (sections.isEmpty()) return;
        final SectionMeta meta = sections.remove(0);
        if (meta.handler() == null) return;
        meta.handler().onSectionExit(this);
    }
    
    public abstract boolean hasFunction(String name);
    
    public abstract Function getFunction(String name);
    
    public abstract void registerFunction(Function function);
    
    public abstract Function assertDefaultLocalFunction(String name);
    
    public void addLibrary(Library provider) {
        this.libraries.add(provider);
    }
    
    public Collection<Library> getLibraries() {
        return libraries;
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public Unit getCurrentUnit() {
        return units.isEmpty() ? null : units.get(0);
    }
    
    public boolean hasCurrentUnit() {
        return !units.isEmpty();
    }
    
    public void destroyUnit() {
        if (units.isEmpty()) return;
        units.remove(0);
    }
    
    public void createUnit(final LanguageElement element) {
        units.add(0, new Unit(element));
    }
    
    public Collection<SyntaxElement> getHandlers(State state, LanguageElement expected) {
        final List<SyntaxElement> elements = new ArrayList<>();
        for (Library library : getLibraries()) {
            elements.addAll(library.getHandlers(state, expected, this));
        }
        return elements;
    }
    
    public Collection<SyntaxElement> getHandlers(State state) {
        return getHandlers(state, getExpected());
    }
    
    public Collection<SyntaxElement> getHandlers() {
        return getHandlers(getState(), getExpected());
    }
    
    public void registerType(Type type) {
        if (type.isArray()) registerType(type.componentType().getSimpleName() + "s", type);
        else registerType(type.getSimpleName(), type);
    }
    
    public String expectedIndent() {
        if (indentUnit() == null) return "";
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent(); i++) {
            builder.append(indentUnit());
        }
        return builder.toString();
    }
    
}
