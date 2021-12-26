/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.*;
import org.byteskript.skript.compiler.structure.*;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.CompiledScript;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public class FileContext extends Context {
    
    final Type type;
    protected final Map<String, Type> types = new HashMap<>();
    protected String indentUnit;
    int indent;
    int lineNumber;
    int lambdaIndex;
    int indexShift;
    boolean sectionHeader;
    LanguageElement expected;
    SyntaxElement currentEffect;
    private HandlerType mode = StandardHandlers.GET;
    final List<Consumer<Context>> endOfLine = new ArrayList<>();
    final Map<HandlerType, List<PropertyAccessGenerator>> usedProperties = new HashMap<>();
    final List<ClassBuilder> suppressedClasses = new ArrayList<>();
    final List<Flag> flags = new ArrayList<>();
    
    ElementTree line;
    ElementTree current;
    protected final List<Function> functions = new ArrayList<>();
    protected List<PreVariable> variables = new ArrayList<>();
    protected final List<ProgrammaticSplitTree> trees = new ArrayList<>();
    
    protected ClassBuilder writer;
    protected FieldBuilder field;
    protected MethodBuilder method;
    
    public FileContext(Type type) {
        this.type = type;
        this.state = CompileState.ROOT;
        this.writer = new ClassBuilder(type, SkriptLangSpec.JAVA_VERSION)
            .addModifiers(Modifier.PUBLIC)
            .addModifiers(Modifier.FINAL)
            .setSuperclass(CompiledScript.class);
        this.addSkriptFunctions();
//        writer.setComputation(1); // todo
    }
    
    private void addSkriptFunctions() {
        try {
            final Class<?> skript = Class.forName("skript");
            final Type owner = new Type(skript);
            for (final Method method : Class.forName("skript").getMethods()) {
                if (!Modifier.isStatic(method.getModifiers())) continue;
                if (!Modifier.isPublic(method.getModifiers())) continue;
                this.functions.add(new Function(method.getName(), owner, CommonTypes.OBJECT, Type.array(CommonTypes.OBJECT, method.getParameterCount()), new Type(method.getReturnType()), Type.of(method.getParameterTypes())));
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Unable to load Skript functions.", ex);
        }
    }
    
    public PostCompileClass[] compile() {
        for (List<PropertyAccessGenerator> value : usedProperties.values()) {
            for (PropertyAccessGenerator generator : value) {
                generator.compile(this);
            }
        }
        final List<PostCompileClass> classes = new ArrayList<>();
        classes.add(new PostCompileClass(writer.compile(), writer.getName(), writer.getInternalName()));
        for (ClassBuilder builder : writer.getSuppressed()) {
            classes.add(new PostCompileClass(builder.compile(), builder.getName(), builder.getInternalName()));
        }
        return classes.toArray(new PostCompileClass[0]);
    }
    
    @Override
    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }
    
    @Override
    public void addFlag(Flag flag) {
        this.flags.add(flag);
    }
    
    @Override
    public void removeFlag(Flag flag) {
        this.flags.remove(flag);
    }
    
    @Override
    public LanguageElement getExpected() {
        return expected;
    }
    
    @Override
    public Collection<Type> getAvailableTypes() {
        return types.values();
    }
    
    @Override
    public Map<String, Type> getTypeMap() {
        return types;
    }
    
    @Override
    public Type getType(String name) {
        return types.get(name);
    }
    
    @Override
    public void registerType(String name, Type type) {
        types.put(name, type);
    }
    
    @Override
    public int indent() {
        return indent;
    }
    
    @Override
    public String indentUnit() {
        return indentUnit;
    }
    
    @Override
    public void setIndentUnit(String string) {
        this.indentUnit = string;
    }
    
    @Override
    public ClassBuilder getBuilder() {
        return writer;
    }
    
    @Override
    public void useSubBuilder(ClassBuilder builder) {
        if (!builder.hasSuppressor() || builder.getSuppressor() != this.writer)
            throw new ScriptCompileError(lineNumber, "Sub-builders must be suppressed by the root class builder.");
        this.writer = builder;
    }
    
    @Override
    public ClassBuilder endSubBuilder() {
        if (!writer.hasSuppressor())
            throw new ScriptCompileError(lineNumber, "Sub-builders must be suppressed by the root class builder.");
        this.writer = writer.getSuppressor();
        return writer;
    }
    
    @Override
    public ClassBuilder addSuppressedBuilder(final Type type) {
        final ClassBuilder builder;
        this.writer.suppress(builder = new ClassBuilder(type, SkriptLangSpec.JAVA_VERSION));
        this.suppressedClasses.add(0, builder);
        return builder;
    }
    
    @Override
    public ClassBuilder getSuppressedBuilder(final Type type) {
        for (final ClassBuilder builder : suppressedClasses) {
            if (builder.getType().equals(type)) return builder;
        }
        return null;
    }
    
    @Override
    public ClassBuilder getSuppressedBuilder() {
        return suppressedClasses.get(0);
    }
    
    @Override
    public MethodBuilder getMethod() {
        return method;
    }
    
    @Override
    public FieldBuilder getField() {
        return field;
    }
    
    @Override
    public void setMethod(MethodBuilder method) {
        this.method = method;
    }
    
    @Override
    public void setField(FieldBuilder field) {
        this.field = field;
    }
    
    @Override
    public int lineNumber() {
        return lineNumber;
    }
    
    @Override
    public SyntaxElement currentEffect() {
        return currentEffect;
    }
    
    @Override
    public PreVariable forceUnspecVariable(PreVariable variable) {
        this.variables.add(variable);
        return variable;
    }
    
    @Override
    public PreVariable getVariable(String name) {
        for (PreVariable variable : variables) {
            if (variable.name().equals(name)) return variable;
        }
        final PreVariable variable = new PreVariable(name);
        this.variables.add(variable);
        return variable;
    }
    
    @Override
    public List<PreVariable> getVariables() {
        return variables;
    }
    
    @Override
    public PreVariable getVariable(int slot) {
        return variables.get(slot);
    }
    
    @Override
    public int slotOf(PreVariable variable) {
        return variables.indexOf(variable);
    }
    
    @Override
    public boolean hasVariable(String name) {
        for (PreVariable variable : variables) {
            if (variable.name().equals(name)) return true;
        }
        return false;
    }
    
    @Override
    public void emptyVariables() {
        this.variables = new ArrayList<>();
    }
    
    @Override
    public int getVariableCount() {
        return variables.size();
    }
    
    @Override
    public ElementTree getLine() {
        return line;
    }
    
    @Override
    public ElementTree getCompileCurrent() {
        return current;
    }
    
    @Override
    public void setCompileCurrent(ElementTree element) {
        this.current = element;
    }
    
    @Override
    public void createTree(ProgrammaticSplitTree tree) {
        this.trees.add(0, tree);
    }
    
    @Override
    public <Tree extends ProgrammaticSplitTree> Tree findTree(Class<Tree> type) {
        for (ProgrammaticSplitTree tree : this.trees) {
            if (type.isInstance(tree)) return (Tree) tree;
        }
        return null;
    }
    
    @Override
    public ProgrammaticSplitTree getTree(SectionMeta meta) {
        for (ProgrammaticSplitTree tree : trees) {
            if (tree.owner() == meta) return tree;
        }
        return null;
    }
    
    @Override
    public ProgrammaticSplitTree getCurrentTree() {
        return trees.isEmpty() ? null : trees.get(0);
    }
    
    @Override
    public void closeAllTrees() {
        for (ProgrammaticSplitTree tree : trees) {
            tree.close(this);
        }
        trees.clear();
    }
    
    @Override
    public void removeTree(ProgrammaticSplitTree tree) {
        this.trees.remove(tree);
    }
    
    @Override
    public void addInnerClass(Type type, int modifiers) {
        this.getBuilder().addInnerClass(type, modifiers);
    }
    
    @Override
    public int getLambdaIndex() {
        return lambdaIndex;
    }
    
    @Override
    public void increaseLambdaIndex() {
        this.lambdaIndex++;
    }
    
    @Override
    public void addSkipInstruction(Consumer<Context> consumer) {
        this.endOfLine.add(consumer);
    }
    
    @Override
    public boolean isSectionHeader() {
        return sectionHeader;
    }
    
    @Override
    public int methodIndexShift() {
        return indexShift++;
    }
    
    @Override
    public boolean hasHandle(String property, HandlerType type) {
        for (final Library library : this.libraries) {
            for (final PropertyHandler handler : library.getProperties()) {
                if (handler.name().equals(property)) return true;
            }
        }
        return false;
    }
    
    @Override
    public MethodErasure useHandle(String property, HandlerType type) {
        this.usedProperties.putIfAbsent(type, new ArrayList<>());
        final List<PropertyAccessGenerator> list = this.usedProperties.get(type);
        for (Library library : this.libraries) {
            sub:
            for (PropertyHandler handler : library.getProperties()) {
                if (!handler.name().equals(property)) continue;
                if (!handler.type().equals(type)) continue;
                int uses = 0;
                for (PropertyAccessGenerator generator : list) {
                    if (!generator.getName().equals(property)) continue;
                    if (!generator.getType().equals(type)) continue;
                    uses++;
                    generator.addUse(handler.holder(), handler.method());
                }
                if (uses == 0) {
                    final PropertyAccessGenerator generator = new PropertyAccessGenerator(type, property);
                    generator.addUse(handler.holder(), handler.method());
                    list.add(generator);
                }
            }
        }
        final Type ret = type.expectReturn() ? CommonTypes.OBJECT : new Type(void.class);
        final Type[] params = type.expectInputs() ? new Type[]{CommonTypes.OBJECT, CommonTypes.OBJECT} : new Type[]{CommonTypes.OBJECT};
        return new MethodErasure(ret, "property_" + type.name() + "$" + property, params);
    }
    
    @Override
    public void setHandlerMode(HandlerType type) {
        this.mode = type;
    }
    
    @Override
    public HandlerType getHandlerMode() {
        return mode;
    }
    
    @Override
    public boolean hasFunction(String name, int arguments) {
        for (Function function : functions) {
            if (function.name().equals(name) && function.arguments().length == arguments) return true;
        }
        return false;
    }
    
    @Override
    public Function getFunction(String name, int arguments) {
        for (Function function : functions) {
            if (function.name().equals(name) && function.arguments().length == arguments) return function;
        }
        return null;
    }
    
    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public void registerFunction(Function function) {
        functions.add(0, function);
    }
    
    @Override
    public Function assertDefaultLocalFunction(String name) {
        if (true) throw new RuntimeException("i need to know what's using this"); // todo
        return new Function(name, type);
    }
}
