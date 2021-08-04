package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.LanguageDefinition;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.foundation.opcodes.JavaVersion;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.Property;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.syntax.code.*;
import mx.kenzie.skript.lang.syntax.code.control.*;
import mx.kenzie.skript.lang.syntax.code.list.ClearList;
import mx.kenzie.skript.lang.syntax.code.list.IndexOfList;
import mx.kenzie.skript.lang.syntax.code.list.ListCreator;
import mx.kenzie.skript.lang.syntax.code.map.*;
import mx.kenzie.skript.lang.syntax.code.relative.IsEqual;
import mx.kenzie.skript.lang.syntax.code.relative.NotEqual;
import mx.kenzie.skript.lang.syntax.code.timing.SecondsExpression;
import mx.kenzie.skript.lang.syntax.entry.Trigger;
import mx.kenzie.skript.lang.syntax.literal.BooleanLiteral;
import mx.kenzie.skript.lang.syntax.literal.IntegerLiteral;
import mx.kenzie.skript.lang.syntax.literal.NoneLiteral;
import mx.kenzie.skript.lang.syntax.literal.StringLiteral;
import mx.kenzie.skript.lang.syntax.member.NoArgsFunctionMember;

import java.util.*;
import java.util.regex.Pattern;

public final class SkriptLangSpec implements LanguageDefinition, Library {
    public static final Pattern LINE_COMMENT = Pattern.compile("//.*(?=(\\R|$|\\n))");
    public static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    public static final Pattern DEAD_SPACE = Pattern.compile("(?<=\\S)[\\t\\f\\v ]+(?=(\\R|$))");
    public static final Pattern IDENTIFIER = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    public static final JavaVersion JAVA_VERSION = JavaVersion.JAVA_8;
    
    static final SkriptLangSpec INSTANCE = new SkriptLangSpec();
    public static final LanguageDefinition LANG = INSTANCE;
    public static final Library LIBRARY = INSTANCE;
    
    final LanguageElement[] grammar = StandardElements.values();
    
    final Map<State, List<SyntaxElement>> syntax = new HashMap<>();
    
    final Type[] types = {
        CommonTypes.CLASS,
        CommonTypes.INTEGER,
        CommonTypes.DOUBLE,
        CommonTypes.NUMBER,
        CommonTypes.BOOLEAN,
        CommonTypes.STRING,
        CommonTypes.OBJECT,
        CommonTypes.OBJECTS,
        CommonTypes.VOID,
        CommonTypes.DURATION,
        CommonTypes.REFERENT,
        CommonTypes.LIST,
        CommonTypes.MAP,
        CommonTypes.METHOD,
        CommonTypes.FIELD
    };
    
    private SkriptLangSpec() {
        for (CompileState value : CompileState.values()) {
            syntax.put(value, new ArrayList<>());
        }
        syntax.get(CompileState.ROOT).addAll(Arrays.asList(
            new NoArgsFunctionMember()
        ));
        syntax.get(CompileState.MEMBER_BODY).addAll(Arrays.asList(
            new Trigger()
        ));
        syntax.get(CompileState.CODE_BODY).addAll(Arrays.asList(
            new PrintEffect(),
            new StopEffect(),
            new WaitEffect(),
            new ReturnEffect(),
            new SetVariableEffect(),
            new SetEffect(),
            new AddEffect(),
            new DeleteEffect(),
            new RemoveEffect(),
            new AssertEffect(),
            new ClearList(),
            new ClearMap()
        ));
        syntax.get(CompileState.STATEMENT).addAll(Arrays.asList(
            new NoneLiteral(),
            new BracketExpression(),
            new BooleanLiteral(),
            new VariableExpression(),
            new NotEqual(),
            new IsEqual(),
            new StringLiteral(),
            new JavaVersionExpression(),
            new SecondsExpression(),
            new IntegerLiteral(),
            new MapCreator(),
            new ListCreator(),
            new IndexOfList(),
            new KeyInMap(),
            new KeysOfMap(),
            new ValuesOfMap()
        ));
    }
    
    @Override
    public String name() {
        return "Skript";
    }
    
    @Override
    public Collection<SyntaxElement> getHandlers(State state, LanguageElement expected, Context context) {
        final List<SyntaxElement> elements = new ArrayList<>(syntax.getOrDefault(state, new ArrayList<>()));
        elements.removeIf(element -> {
            if (expected != null && element.getType() != expected) return true;
            return !element.allowedIn(state, context);
        });
        return elements;
    }
    
    @Override
    public Collection<Property> getProperties() {
        final List<Property> list = new ArrayList<>();
        for (SyntaxElement element : getSyntax()) {
            if (element instanceof Property property) list.add(property);
        }
        return list;
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
        return grammar;
    }
    
    @Override
    public Type[] getTypes() {
        return types;
    }
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        return Collections.emptyList();
    }
    
    @Override
    public String sourceFileExt() {
        return "bsk";
    }
}
