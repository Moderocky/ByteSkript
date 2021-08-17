package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.*;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.api.*;
import mx.kenzie.skript.compiler.structure.*;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.runtime.internal.CompiledScript;

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
    
    ElementTree line;
    ElementTree current;
    protected final List<Function> functions = new ArrayList<>();
    protected final List<PreVariable> variables = new ArrayList<>();
    protected final List<ProgrammaticSplitTree> trees = new ArrayList<>();
    
    protected final ClassBuilder writer;
    protected FieldBuilder field;
    protected MethodBuilder method;
    
    public FileContext(Type type) {
        this.type = type;
        this.state = CompileState.ROOT;
        this.writer = new ClassBuilder(type, SkriptLangSpec.JAVA_VERSION)
            .addModifiers(Modifier.PUBLIC)
            .addModifiers(Modifier.FINAL)
            .setSuperclass(CompiledScript.class);
//        writer.setComputation(1); // todo
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
    public ClassBuilder addSuppressedBuilder(final Type type) {
        final ClassBuilder builder;
        this.writer.suppress(builder = new ClassBuilder(type, SkriptLangSpec.JAVA_VERSION));
        return builder;
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
        this.variables.clear();
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
        for (ProgrammaticSplitTree tree : trees.toArray(new ProgrammaticSplitTree[0])) {
            tree.close(this);
        }
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
    public boolean hasFunction(String name) {
        for (Function function : functions) {
            if (function.name().equals(name)) return true;
        }
        return false;
    }
    
    @Override
    public Function getFunction(String name) {
        for (Function function : functions) {
            if (function.name().equals(name)) return function;
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
        return new Function(name, type);
    }
}
