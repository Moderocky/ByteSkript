package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.LanguageDefinition;
import mx.kenzie.foundation.opcodes.JavaVersion;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.ModifiableLibrary;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.comparison.*;
import mx.kenzie.skript.lang.syntax.control.*;
import mx.kenzie.skript.lang.syntax.entry.JavaRelay;
import mx.kenzie.skript.lang.syntax.entry.Trigger;
import mx.kenzie.skript.lang.syntax.entry.Verify;
import mx.kenzie.skript.lang.syntax.event.AnyLoadEvent;
import mx.kenzie.skript.lang.syntax.event.CurrentEventExpression;
import mx.kenzie.skript.lang.syntax.event.LoadEvent;
import mx.kenzie.skript.lang.syntax.flow.*;
import mx.kenzie.skript.lang.syntax.function.*;
import mx.kenzie.skript.lang.syntax.generic.*;
import mx.kenzie.skript.lang.syntax.io.IOHandlers;
import mx.kenzie.skript.lang.syntax.list.ClearList;
import mx.kenzie.skript.lang.syntax.list.IndexOfList;
import mx.kenzie.skript.lang.syntax.list.ListCreator;
import mx.kenzie.skript.lang.syntax.literal.BooleanLiteral;
import mx.kenzie.skript.lang.syntax.literal.IntegerLiteral;
import mx.kenzie.skript.lang.syntax.literal.NoneLiteral;
import mx.kenzie.skript.lang.syntax.literal.StringLiteral;
import mx.kenzie.skript.lang.syntax.map.ClearMap;
import mx.kenzie.skript.lang.syntax.map.KeyInMap;
import mx.kenzie.skript.lang.syntax.map.MapCreator;
import mx.kenzie.skript.lang.syntax.maths.*;
import mx.kenzie.skript.lang.syntax.timing.MilliSecondsExpression;
import mx.kenzie.skript.lang.syntax.timing.SecondsExpression;
import mx.kenzie.skript.lang.syntax.timing.WaitEffect;
import mx.kenzie.skript.runtime.type.DataList;
import mx.kenzie.skript.runtime.type.DataMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class SkriptLangSpec extends ModifiableLibrary implements LanguageDefinition, Library {
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
    
    private SkriptLangSpec() {
        super("Skript");
        registerTypes(
            CommonTypes.CLASS,
            CommonTypes.TYPE,
            CommonTypes.INTEGER,
            CommonTypes.DOUBLE,
            CommonTypes.NUMBER,
            CommonTypes.BOOLEAN,
            CommonTypes.STRING,
            CommonTypes.OBJECT,
            CommonTypes.OBJECTS,
            CommonTypes.VOID,
            CommonTypes.EVENT,
            CommonTypes.DURATION,
            CommonTypes.REFERENT,
            CommonTypes.LIST,
            CommonTypes.MAP,
            CommonTypes.THROWABLE,
            CommonTypes.ERROR,
            CommonTypes.EXECUTABLE,
            CommonTypes.METHOD,
            CommonTypes.FIELD
        );
        registerSyntax(CompileState.ROOT,
//            new InitClassMember(),
            new FunctionMember(),
            new NoArgsFunctionMember()
        );
        registerSyntax(CompileState.MEMBER_BODY,
            new Verify(),
            new Trigger()
        );
        registerSyntax(CompileState.CODE_BODY,
            new PrintEffect(),
            new StopEffect(),
            new WaitEffect(),
            new ReturnEffect(),
            new WhileSection(),
            new IfSection(),
            new ElseIfSection(),
            new ElseSection(),
            new SetEffect(),
            new AddEffect(),
            new DeleteEffect(),
            new RemoveEffect(),
            new AssertEffect(),
            new ExitEffect(),
            new RunAsyncEffect(),
            new RunEffect(),
            new BreakIfEffect(),
            new BreakEffect(),
            new ClearList(),
            new ClearMap()
        );
        registerSyntax(CompileState.STATEMENT,
            new NoneLiteral(),
            new BracketExpression(),
            new BooleanLiteral(),
            new VariableExpression(),
            new IsOfType(),
            new Exists(),
            new GTEQ(),
            new LTEQ(),
            new GT(),
            new LT(),
            new NotEqual(),
            new IsEqual(),
            new Contains(),
            new StringLiteral(),
            new RunnableSection(),
            new PropertyExpression(),
            new SystemInputExpression(),
            new FunctionExpression(),
            new CurrentScriptExpression(),
            new CurrentEventExpression(),
            new NoArgsFunctionExpression(),
            new JavaVersionExpression(),
            new MultiplyExpression(),
            new DivideExpression(),
            new SquareRootExpression(),
            new AddExpression(),
            new SubtractExpression(),
            new DynamicFunctionExpression(),
            new MilliSecondsExpression(),
            new SecondsExpression(),
            new IntegerLiteral(),
            new MapCreator(),
            new ListCreator(),
            new IndexOfList(),
            new KeyInMap(),
            new TypeExpression()
        );
        registerEvents(
            new LoadEvent(),
            new AnyLoadEvent()
        );
        generateSyntaxFrom(IOHandlers.class);
        generateSyntaxFrom(JavaRelay.class);
        try {
            registerProperty("keys", StandardHandlers.GET, DataMap.class.getMethod("getKeys", Map.class));
            registerProperty("values", StandardHandlers.GET, DataMap.class.getMethod("getValues", Map.class));
            registerProperty("size", StandardHandlers.GET, DataMap.class.getMethod("getSize", Map.class));
            registerProperty("size", StandardHandlers.GET, DataList.class.getMethod("getSize", Collection.class));
            registerProperty("class", StandardHandlers.GET, Object.class.getMethod("getClass"));
            registerProperty("name", StandardHandlers.GET, Class.class.getMethod("getSimpleName"));
            registerProperty("path", StandardHandlers.GET, Class.class.getMethod("getName"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String sourceFileExt() {
        return "bsk";
    }
}
