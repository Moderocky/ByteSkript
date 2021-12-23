package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.LanguageDefinition;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.foundation.opcodes.JavaVersion;
import mx.kenzie.skript.api.*;
import mx.kenzie.skript.app.ScriptRunner;
import mx.kenzie.skript.app.SimpleThrottleController;
import mx.kenzie.skript.app.SkriptApp;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.error.ScriptRuntimeError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.comparison.*;
import mx.kenzie.skript.lang.syntax.control.AddEffect;
import mx.kenzie.skript.lang.syntax.control.DeleteEffect;
import mx.kenzie.skript.lang.syntax.control.RemoveEffect;
import mx.kenzie.skript.lang.syntax.control.SetEffect;
import mx.kenzie.skript.lang.syntax.entry.JavaRelay;
import mx.kenzie.skript.lang.syntax.entry.Trigger;
import mx.kenzie.skript.lang.syntax.entry.Verify;
import mx.kenzie.skript.lang.syntax.event.AnyLoadEvent;
import mx.kenzie.skript.lang.syntax.event.CurrentEventExpression;
import mx.kenzie.skript.lang.syntax.event.LoadEvent;
import mx.kenzie.skript.lang.syntax.flow.*;
import mx.kenzie.skript.lang.syntax.flow.conditional.ElseIfSection;
import mx.kenzie.skript.lang.syntax.flow.conditional.ElseSection;
import mx.kenzie.skript.lang.syntax.flow.conditional.IfSection;
import mx.kenzie.skript.lang.syntax.flow.execute.*;
import mx.kenzie.skript.lang.syntax.flow.lambda.RunnableSection;
import mx.kenzie.skript.lang.syntax.flow.lambda.SupplierSection;
import mx.kenzie.skript.lang.syntax.flow.loop.LoopInSection;
import mx.kenzie.skript.lang.syntax.flow.loop.LoopTimesSection;
import mx.kenzie.skript.lang.syntax.flow.loop.WhileSection;
import mx.kenzie.skript.lang.syntax.function.*;
import mx.kenzie.skript.lang.syntax.generic.*;
import mx.kenzie.skript.lang.syntax.list.ClearList;
import mx.kenzie.skript.lang.syntax.list.ImplicitArrayCreator;
import mx.kenzie.skript.lang.syntax.list.IndexOfList;
import mx.kenzie.skript.lang.syntax.list.ListCreator;
import mx.kenzie.skript.lang.syntax.literal.*;
import mx.kenzie.skript.lang.syntax.map.ClearMap;
import mx.kenzie.skript.lang.syntax.map.KeyInMap;
import mx.kenzie.skript.lang.syntax.map.MapCreator;
import mx.kenzie.skript.lang.syntax.maths.*;
import mx.kenzie.skript.lang.syntax.timing.*;
import mx.kenzie.skript.lang.syntax.variable.AtomicVariableExpression;
import mx.kenzie.skript.lang.syntax.variable.GlobalVariableExpression;
import mx.kenzie.skript.lang.syntax.variable.ThreadVariableExpression;
import mx.kenzie.skript.lang.syntax.variable.VariableExpression;
import mx.kenzie.skript.runtime.internal.IOHandlers;
import mx.kenzie.skript.runtime.type.DataList;
import mx.kenzie.skript.runtime.type.DataMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class SkriptLangSpec extends ModifiableLibrary implements LanguageDefinition, Library {
    public static final Pattern LINE_COMMENT = Pattern.compile("//.*(?=(\\R|$|\\n))");
    public static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    public static final Pattern DEAD_SPACE = Pattern.compile("(?<=\\S)[\\t\\f\\v ]+(?=(\\R|$))");
    public static final Pattern IDENTIFIER = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    public static final JavaVersion JAVA_VERSION = JavaVersion.JAVA_17;
    
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
            new SleepEffect(),
            new WakeEffect(),
            new WaitEffect(),
            new ReturnEffect(),
            new WhileSection(),
            new LoopTimesSection(),
            new LoopInSection(),
            new IfSection(),
            new ElseIfSection(),
            new ElseSection(),
            new SetEffect(),
            new AddEffect(),
            new DeleteEffect(),
            new RemoveEffect(),
            new AssertEffect(),
            new ExitEffect(),
            new ExitThreadEffect(),
            new RunWithAsyncEffect(),
            new RunWithEffect(),
            new RunAsyncEffect(),
            new RunEffect(),
            new BreakLoopEffect(),
            new ContinueEffect(),
            new BreakIfEffect(),
            new BreakEffect(),
            new ClearList(),
            new ClearMap()
        );
        registerSyntax(CompileState.STATEMENT,
            new NoneLiteral(),
            new ImplicitArrayCreator(),
            new BracketExpression(),
            new BooleanLiteral(),
            new ThreadVariableExpression(),
            new AtomicVariableExpression(),
            new GlobalVariableExpression(),
            new VariableExpression(),
            new IsArray(),
            new IsOfType(),
            new Exists(),
            new GTEQ(),
            new LTEQ(),
            new GT(),
            new LT(),
            new NotEqual(),
            new IsEqual(),
            new Contains(),
            new TernaryOtherwiseExpression(),
            new BinaryOtherwiseExpression(),
            new StringLiteral(),
            new SupplierSection(),
            new RunnableSection(),
            new ThreadExpression(),
            new NewLineExpression(),
            new ResultOfExpression(), // must try before property
            new PropertyExpression(),
            new SystemInputExpression(),
            new SystemPropertyExpression(),
            new ExternalFunctionExpression(),
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
            new MillisecondsExpression(),
            new SecondsExpression(),
            new MinutesExpression(),
            new HoursExpression(),
            new DaysExpression(),
            new WeeksExpression(),
            new MonthsExpression(),
            new YearsExpression(),
            new IntegerLiteral(),
            new LongLiteral(),
            new FloatLiteral(),
            new DoubleLiteral(),
            new DoubleLiteral(),
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
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        final List<PostCompileClass> runtime = new ArrayList<>();
        try {
            for (final Class<?> source : this.findClasses("mx/kenzie/skript/runtime/")) {
                runtime.add(this.getData(source));
            }
            for (final Class<?> source : this.findClasses("mx/kenzie/skript/error/")) {
                runtime.add(this.getData(source));
            }
            for (final Class<?> source : this.findClasses("mx/kenzie/mimic/")) {
                runtime.add(this.getData(source));
            }
            for (final Class<?> source : this.findClasses("mx/kenzie/mirror/")) {
                runtime.add(this.getData(source));
            }
            for (final Class<?> source : this.findClasses("org/objectweb/asm/")) {
                runtime.add(this.getData(source));
            }
            runtime.add(this.getData(Event.class));
            runtime.add(this.getData(Library.class));
            runtime.add(this.getData(Class.forName("skript")));
            runtime.add(this.getData(SkriptCompiler.class));
            runtime.add(this.getData(Compiler.class));
            runtime.add(this.getData(ScriptRunner.class));
            runtime.add(this.getData(SimpleThrottleController.class));
        } catch (IOException | ClassNotFoundException ex) {
            throw new ScriptCompileError(-1, "Unable to add runtime to compiled classes.", ex);
        }
        return runtime;
    }
    
    private PostCompileClass getData(final Class<?> type) throws IOException {
        return new PostCompileClass(getSource(type), type.getName(), new Type(type).internalName());
    }
    
    private byte[] getSource(final Class<?> cls) throws IOException {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream(cls.getName()
            .replace('.', '/') + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        }
    }
    
    private Class<?>[] findClasses(final String namespace) throws IOException, ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        final CodeSource src = ScriptRunner.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            final URL jar = src.getLocation();
            try (final ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                while (true) {
                    final ZipEntry entry = zip.getNextEntry();
                    if (entry == null) break;
                    if (entry.isDirectory()) continue;
                    final String name = entry.getName();
                    if (name.startsWith(namespace)) {
                        final Class<?> data = Class.forName(name
                            .substring(0, name.length() - 6)
                            .replace("/", "."), false, SkriptApp.class.getClassLoader());
                        classes.add(data);
                    }
                }
            }
        } else {
            throw new ScriptRuntimeError("Unable to access source.");
        }
        return classes.toArray(new Class[0]);
    }
    
}
