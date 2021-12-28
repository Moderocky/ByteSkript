/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.*;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.structure.*;

import java.util.*;
import java.util.function.Consumer;

public abstract class Context {
    
    protected final List<Library> libraries = new ArrayList<>();
    protected final List<Unit> units = new ArrayList<>();
    protected final List<SectionMeta> sections = new ArrayList<>();
    protected ErrorDetails error;
    protected State state;
    
    public ErrorDetails getError() {
        return error;
    }
    
    public String getStoredVariableName() {
        return storedVariableName;
    }
    
    public void setStoredVariableName(String storedVariableName) {
        this.storedVariableName = storedVariableName;
    }
    
    protected String storedVariableName;
    
    public abstract boolean hasFlag(Flag flag);
    
    public abstract void addFlag(Flag flag);
    
    public abstract void removeFlag(Flag flag);
    
    public abstract LanguageElement getExpected();
    
    public abstract Collection<Type> getAvailableTypes();
    
    public abstract Map<String, Type> getTypeMap();
    
    public abstract Type getType();
    
    public abstract Type getType(String name);
    
    public abstract void registerType(String name, Type type);
    
    public abstract int indent();
    
    public abstract String indentUnit();
    
    public abstract void setIndentUnit(String string);
    
    public abstract ClassBuilder getBuilder();
    
    public abstract void useSubBuilder(final ClassBuilder builder);
    
    public abstract ClassBuilder endSubBuilder();
    
    public abstract ClassBuilder addSuppressedBuilder(final Type type);
    
    public abstract ClassBuilder getSuppressedBuilder(Type type);
    
    public abstract ClassBuilder getSuppressedBuilder();
    
    public abstract MethodBuilder getMethod();
    
    public abstract FieldBuilder getField();
    
    public abstract void setMethod(MethodBuilder method);
    
    public abstract void setField(FieldBuilder field);
    
    public abstract int lineNumber();
    
    public abstract SyntaxElement currentEffect();
    
    public abstract PreVariable forceUnspecVariable(PreVariable variable);
    
    public abstract PreVariable getVariable(String name);
    
    public abstract PreVariable getVariable(int slot);
    
    public abstract void emptyVariables();
    
    public abstract int getVariableCount();
    
    public abstract Collection<PreVariable> getVariables();
    
    public abstract int slotOf(PreVariable variable);
    
    public abstract boolean hasVariable(String name);
    
    public abstract ElementTree getLine();
    
    public abstract ElementTree getCompileCurrent();
    
    public abstract void setCompileCurrent(ElementTree element);
    
    public abstract void createTree(ProgrammaticSplitTree tree);
    
    public abstract <Tree extends ProgrammaticSplitTree> Tree findTree(Class<Tree> type);
    
    public abstract ProgrammaticSplitTree getTree(SectionMeta meta);
    
    public abstract ProgrammaticSplitTree getCurrentTree();
    
    public abstract void closeAllTrees();
    
    public abstract void removeTree(ProgrammaticSplitTree tree);
    
    public abstract void addInnerClass(Type type, int modifiers);
    
    public abstract int getLambdaIndex();
    
    public abstract void increaseLambdaIndex();
    
    public abstract void addSkipInstruction(Consumer<Context> consumer);
    
    public abstract boolean isSectionHeader();
    
    public abstract int methodIndexShift();
    
    public abstract boolean hasHandle(String property, HandlerType type);
    
    public abstract MethodErasure useHandle(String property, HandlerType type);
    
    public abstract void setHandlerMode(HandlerType type);
    
    public abstract HandlerType getHandlerMode();
    
    public MultiLabel getSectionBreak() {
        return getCurrentTree().getEnd();
    }
    
    public void addSection(Section handler) {
        sections.add(0, new SectionMeta(handler));
    }
    
    public void appendSection(Section handler) {
        if (sections.isEmpty()) addSection(handler);
        else sections.get(0).getHandlers().add(handler);
    }
    
    public SectionMeta getSection() {
        if (sections.isEmpty()) return null;
        return sections.get(0);
    }
    
    public SectionMeta getSection(int index) {
        if (sections.isEmpty()) return null;
        return sections.get(index);
    }
    
    public Section getParent() {
        if (sections.isEmpty()) return null;
        return sections.get(0).handler();
    }
    
    public void destroySection() {
        if (sections.isEmpty()) return;
        final SectionMeta meta = sections.remove(0);
        final Section[] handlers = meta.getHandlers().toArray(new Section[0]);
        for (int i = handlers.length - 1; i >= 0; i--) {
            final Section section = handlers[i];
            section.onSectionExit(this, meta);
        }
    }
    
    public abstract boolean hasFunction(String name, int arguments);
    
    public abstract Function getFunction(String name, int arguments);
    
    public Function getDefaultFunction(String name, int arguments) {
        final Function function = getFunction(name, arguments);
        if (function != null) return function;
        final Type[] types = new Type[arguments];
        Arrays.fill(types, CommonTypes.OBJECT); // assert all types are actually object :)
        return new Function(name, getType(), CommonTypes.OBJECT, types);
    }
    
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
