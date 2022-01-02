/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.LanguageDefinition;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.foundation.opcodes.JavaVersion;
import org.byteskript.skript.api.*;
import org.byteskript.skript.app.ScriptRunner;
import org.byteskript.skript.app.SimpleThrottleController;
import org.byteskript.skript.app.SkriptApp;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.syntax.comparison.*;
import org.byteskript.skript.lang.syntax.control.AddEffect;
import org.byteskript.skript.lang.syntax.control.DeleteEffect;
import org.byteskript.skript.lang.syntax.control.RemoveEffect;
import org.byteskript.skript.lang.syntax.control.SetEffect;
import org.byteskript.skript.lang.syntax.entry.*;
import org.byteskript.skript.lang.syntax.entry.syntax.*;
import org.byteskript.skript.lang.syntax.event.AnyLoadEvent;
import org.byteskript.skript.lang.syntax.event.CurrentEventExpression;
import org.byteskript.skript.lang.syntax.event.LoadEvent;
import org.byteskript.skript.lang.syntax.flow.*;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseIfSection;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseSection;
import org.byteskript.skript.lang.syntax.flow.conditional.IfSection;
import org.byteskript.skript.lang.syntax.flow.error.CatchSection;
import org.byteskript.skript.lang.syntax.flow.error.TryEffect;
import org.byteskript.skript.lang.syntax.flow.error.TrySection;
import org.byteskript.skript.lang.syntax.flow.execute.*;
import org.byteskript.skript.lang.syntax.flow.lambda.RunnableSection;
import org.byteskript.skript.lang.syntax.flow.lambda.SupplierSection;
import org.byteskript.skript.lang.syntax.flow.loop.LoopInSection;
import org.byteskript.skript.lang.syntax.flow.loop.LoopTimesSection;
import org.byteskript.skript.lang.syntax.flow.loop.WhileSection;
import org.byteskript.skript.lang.syntax.function.*;
import org.byteskript.skript.lang.syntax.generic.*;
import org.byteskript.skript.lang.syntax.list.ClearList;
import org.byteskript.skript.lang.syntax.list.ImplicitArrayCreator;
import org.byteskript.skript.lang.syntax.list.IndexOfList;
import org.byteskript.skript.lang.syntax.list.ListCreator;
import org.byteskript.skript.lang.syntax.literal.*;
import org.byteskript.skript.lang.syntax.map.KeyInMap;
import org.byteskript.skript.lang.syntax.map.MapCreator;
import org.byteskript.skript.lang.syntax.maths.*;
import org.byteskript.skript.lang.syntax.script.*;
import org.byteskript.skript.lang.syntax.timing.*;
import org.byteskript.skript.lang.syntax.type.*;
import org.byteskript.skript.lang.syntax.type.property.FinalEntry;
import org.byteskript.skript.lang.syntax.type.property.LocalEntry;
import org.byteskript.skript.lang.syntax.type.property.PropertyMember;
import org.byteskript.skript.lang.syntax.type.property.TypeEntry;
import org.byteskript.skript.lang.syntax.variable.AtomicVariableExpression;
import org.byteskript.skript.lang.syntax.variable.GlobalVariableExpression;
import org.byteskript.skript.lang.syntax.variable.ThreadVariableExpression;
import org.byteskript.skript.lang.syntax.variable.VariableExpression;
import org.byteskript.skript.runtime.internal.IOHandlers;
import org.byteskript.skript.runtime.type.DataList;
import org.byteskript.skript.runtime.type.DataMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.byteskript.skript.lang.handler.StandardHandlers.GET;

public final class SkriptLangSpec extends ModifiableLibrary implements LanguageDefinition, Library {
    public static final Pattern IDENTIFIER = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    public static final JavaVersion JAVA_VERSION = JavaVersion.JAVA_17;
    
    static final SkriptLangSpec INSTANCE = new SkriptLangSpec();
    public static final Library LIBRARY = INSTANCE;
    
    final LanguageElement[] grammar = StandardElements.values();
    
    final Map<State, List<SyntaxElement>> syntax = new HashMap<>();
    
    public LanguageElement[] getGrammar() {
        return grammar;
    }
    
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
            new TypeMember(),
            new TemplateTypeMember(),
            new PropertyMember(),
            new FunctionMember(),
            new NoArgsFunctionMember(),
            new Template()
        );
        registerSyntax(CompileState.MEMBER_BODY,
            new Verify(),
            new Trigger(),
            new ReturnType(),
            new SyntaxEntry(),
            new EffectEntry(),
            new ExpressionEntry(),
            new PropertyEntry(),
            new ModeEntry(),
            new TypeEntry(),
            new LocalEntry(),
            new FinalEntry()
        );
        registerSyntax(CompileState.CODE_BODY,
            new PrintEffect(),
            new StopEffect(),
            new SleepEffect(),
            new WakeEffect(),
            new WaitEffect(),
            new ReturnEffect(),
            new WhileSection(),
            new TrySection(),
            new CatchSection(),
            new LoopTimesSection(),
            new LoopInSection(),
            new IfSection(),
            new ElseIfSection(),
            new ElseSection(),
            new SetEffect(),
            new AddEffect(),
            new DeleteEffect(),
            new RemoveEffect(),
            new AssertWithErrorEffect(),
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
            new TryEffect(),
            new ClearList(),
            new LoadScriptEffect(),
            new UnloadScriptEffect()
        );
        registerSyntax(CompileState.STATEMENT,
            new NoneLiteral(),
            new ImplicitArrayCreator(),
            new BracketExpression(),
            new BooleanLiteral(),
            new ThisThingExpression(),
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
            new Matches(),
            new TernaryOtherwiseExpression(),
            new BinaryOtherwiseExpression(),
            new StringLiteral(),
            new RegexLiteral(),
            new SupplierSection(),
            new RunnableSection(),
            new ThreadExpression(),
            new NewLineExpression(),
            new ResultOfExpression(), // must try before property
            new PropertyExpression(),
            new SystemInputExpression(),
            new SystemPropertyExpression(),
            new ExternalFunctionExpression(),
            new PropertyFunctionExpression(),
            new FunctionExpression(),
            new CompilerExpression(),
            new LoadedScriptsExpression(),
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
            new TypeCreator(),
            new TypeExpression()
        );
        registerEvents(
            new LoadEvent(),
            new AnyLoadEvent()
        );
        generateSyntaxFrom(IOHandlers.class);
        generateSyntaxFrom(JavaRelay.class);
        try {
            registerProperty("keys", GET, DataMap.class.getMethod("getKeys", Map.class));
            registerProperty("values", GET, DataMap.class.getMethod("getValues", Map.class));
            registerProperty("size", GET, DataMap.class.getMethod("getSize", Map.class));
            registerProperty("size", GET, DataList.class.getMethod("getSize", Collection.class));
            registerProperty("class", GET, Object.class.getMethod("getClass"));
            registerProperty("name", GET, Class.class.getMethod("getSimpleName"));
            registerProperty("path", GET, Class.class.getMethod("getName"));
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
            for (final Class<?> source : this.findClasses("org/byteskript/skript/runtime/")) {
                runtime.add(this.getData(source));
            }
            for (final Class<?> source : this.findClasses("org/byteskript/skript/error/")) {
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
            runtime.add(this.getData(BridgeCompiler.class));
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
