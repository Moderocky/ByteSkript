/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.LanguageDefinition;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.foundation.opcodes.JavaVersion;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.app.ScriptRunner;
import org.byteskript.skript.app.SimpleThrottleController;
import org.byteskript.skript.app.SkriptApp;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.syntax.comparison.*;
import org.byteskript.skript.lang.syntax.config.EffectSaveConfig;
import org.byteskript.skript.lang.syntax.config.ExprConfigFileSection;
import org.byteskript.skript.lang.syntax.config.ExprKeyInConfig;
import org.byteskript.skript.lang.syntax.config.ExprNewConfig;
import org.byteskript.skript.lang.syntax.control.EffectAdd;
import org.byteskript.skript.lang.syntax.control.EffectDelete;
import org.byteskript.skript.lang.syntax.control.EffectRemove;
import org.byteskript.skript.lang.syntax.control.EffectSet;
import org.byteskript.skript.lang.syntax.dictionary.EffectImportFunction;
import org.byteskript.skript.lang.syntax.dictionary.EffectUseLibrary;
import org.byteskript.skript.lang.syntax.dictionary.EffectImportType;
import org.byteskript.skript.lang.syntax.dictionary.MemberDictionary;
import org.byteskript.skript.lang.syntax.entry.*;
import org.byteskript.skript.lang.syntax.entry.syntax.*;
import org.byteskript.skript.lang.syntax.event.EventAnyLoad;
import org.byteskript.skript.lang.syntax.event.EventLoad;
import org.byteskript.skript.lang.syntax.event.ExprCurrentEvent;
import org.byteskript.skript.lang.syntax.flow.*;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseIfSection;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseSection;
import org.byteskript.skript.lang.syntax.flow.conditional.IfSection;
import org.byteskript.skript.lang.syntax.flow.error.CatchSection;
import org.byteskript.skript.lang.syntax.flow.error.EffectTry;
import org.byteskript.skript.lang.syntax.flow.error.TrySection;
import org.byteskript.skript.lang.syntax.flow.execute.*;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprRunnableSection;
import org.byteskript.skript.lang.syntax.flow.lambda.ExprSupplierSection;
import org.byteskript.skript.lang.syntax.flow.loop.EffectLoopInSection;
import org.byteskript.skript.lang.syntax.flow.loop.EffectLoopTimesSection;
import org.byteskript.skript.lang.syntax.flow.loop.EffectWhileSection;
import org.byteskript.skript.lang.syntax.function.*;
import org.byteskript.skript.lang.syntax.generic.*;
import org.byteskript.skript.lang.syntax.list.*;
import org.byteskript.skript.lang.syntax.literal.*;
import org.byteskript.skript.lang.syntax.map.ExprKeyInMap;
import org.byteskript.skript.lang.syntax.map.ExprNewMap;
import org.byteskript.skript.lang.syntax.maths.*;
import org.byteskript.skript.lang.syntax.script.*;
import org.byteskript.skript.lang.syntax.test.EffectTest;
import org.byteskript.skript.lang.syntax.timing.*;
import org.byteskript.skript.lang.syntax.type.*;
import org.byteskript.skript.lang.syntax.type.property.EntryFinal;
import org.byteskript.skript.lang.syntax.type.property.EntryLocal;
import org.byteskript.skript.lang.syntax.type.property.EntryProperty;
import org.byteskript.skript.lang.syntax.type.property.EntryType;
import org.byteskript.skript.lang.syntax.variable.ExprVariable;
import org.byteskript.skript.lang.syntax.variable.ExprVariableAtomic;
import org.byteskript.skript.lang.syntax.variable.ExprVariableGlobal;
import org.byteskript.skript.lang.syntax.variable.ExprVariableThread;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.IOHandlers;
import org.byteskript.skript.runtime.type.DataList;
import org.byteskript.skript.runtime.type.DataMap;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    
    private SkriptLangSpec() {
        super("Skript");
        this.registerTypes(
            CommonTypes.CLASS,
            CommonTypes.TYPE,
            CommonTypes.INTEGER,
            CommonTypes.DOUBLE,
            CommonTypes.FLOAT,
            CommonTypes.LONG,
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
        registerConverter(String.class, Integer.class, Integer::valueOf);
        registerConverter(String.class, Double.class, Double::valueOf);
        registerConverter(String.class, Float.class, Float::valueOf);
        registerConverter(String.class, Long.class, Long::valueOf);
        registerConverter(String.class, Number.class, Double::valueOf);
        registerConverter(String.class, Boolean.class, Boolean::valueOf);
        registerConverter(String.class, Error.class, Error::new);
        registerConverter(String.class, File.class, File::new);
        registerConverter(String.class, Class.class, Skript::findAnyClass);
        registerConverter(Object[].class, DataList.class, DataList::of);
        registerConverter(Collection.class, Object[].class, Collection::toArray);
        registerConverter(File.class, OutputStream.class, FileOutputStream::new);
        registerConverter(File.class, InputStream.class, FileInputStream::new);
        registerConverter(Object.class, String.class, Object::toString);
        registerSyntax(CompileState.ROOT,
            new MemberType(),
            new MemberTemplateType(),
            new EntryProperty(),
            new MemberFunction(),
            new MemberFunctionNoArgs(),
            new EntryTemplate(),
            new EntryExtends(),
            new MemberDictionary(),
            new MemberEvery()
        );
        registerSyntax(CompileState.MEMBER_BODY,
            new EntryVerifySection(),
            new EntryTriggerSection(),
            new EntryParameters(),
            new EntryReturn(),
            new EntrySyntax(),
            new EntrySyntaxEffect(),
            new EntrySyntaxExpression(),
            new EntrySyntaxProperty(),
            new EntrySyntaxMode(),
            new EntryType(),
            new EntryLocal(),
            new EntryFinal()
        );
        registerSyntax(CompileState.CODE_BODY,
            new EffectPrint(),
            new EffectStop(),
            new EffectSleep(),
            new EffectWake(),
            new EffectWait(),
            new EffectReturn(),
            new EffectMonitorSection(),
            new EffectWhileSection(),
            new TrySection(),
            new CatchSection(),
            new EffectLoopTimesSection(),
            new EffectLoopInSection(),
            new IfSection(),
            new ElseIfSection(),
            new ElseSection(),
            new EffectSet(),
            new EffectAdd(),
            new EffectDelete(),
            new EffectRemove(),
            new EffectSaveConfig(),
            new EffectAssertWithError(),
            new EffectAssert(),
            new EffectExit(),
            new EffectExitThread(),
            new EffectRunWithAsync(),
            new EffectRunWith(),
            new EffectRunAsync(),
            new EffectWaitFor(),
            new EffectRun(),
            new EffectBreakLoop(),
            new EffectContinue(),
            new EffectBreakIf(),
            new EffectBreak(),
            new EffectTest(),
            new EffectTry(),
            new EffectClearList(),
            new EffectLoadScript(),
            new EffectUnloadScript(),
            new EffectUseLibrary(),
            new EffectImportType(),
            new EffectImportFunction()
        );
        registerSyntax(CompileState.STATEMENT,
            new NoneLiteral(),
            new StringLiteral(),
            new RegexLiteral(),
            new IntegerLiteral(),
            new LongLiteral(),
            new FloatLiteral(),
            new DoubleLiteral(),
            new BooleanLiteral()
        );
        registerSyntax(CompileState.STATEMENT,
            new ExprVariableThread(),
            new ExprVariableAtomic(),
            new ExprVariableGlobal(),
            new ExprVariable()
        );
        registerSyntax(CompileState.STATEMENT,
            new ExprIsArray(),
            new ExprIsOfType(),
            new ExprExists(),
            new ExprGTEQ(),
            new ExprLTEQ(),
            new ExprGT(),
            new ExprLT(),
            new ExprNotEqual(),
            new ExprIsEqual(),
            new ExprContains(),
            new ExprMatches()
        );
        registerSyntax(CompileState.STATEMENT,
            new ExprNewArray(),
            new ExprBracket(),
            new ExprThisThing(),
            new ExprTernaryOtherwise(),
            new ExprBinaryOtherwise(),
            new ExprSupplierSection(),
            new ExprRunnableSection(),
            new ExprThread(),
            new ExprNewLine(),
            new ExprResult(), // must try before property
            new ExprSizeOfList(), // must try before property
            new ExprProperty(),
            new ExprConverter(),
            new ExprSystemInput(),
            new ExprSystemProperty(),
            new ExprFunctionExternal(),
            new ExprFunctionProperty(),
            new ExprFunction(),
            new ExprCompiler(),
            new ExprLoadedScripts(),
            new ExprCurrentScript(),
            new ExprCurrentEvent(),
            new ExprFunctionNoArgs(),
            new ExprJavaVersion(),
            new ExprMultiply(),
            new ExprDivide(),
            new ExprSquareRoot(),
            new ExprAdd(),
            new ExprSubtract(),
            new ExprFunctionDynamic(),
            new ExprMilliseconds(),
            new ExprSeconds(),
            new ExprMinutes(),
            new ExprHours(),
            new ExprDays(),
            new ExprWeeks(),
            new ExprMonths(),
            new ExprYears(),
            new ExprConfigFileSection(),
            new ExprNewConfig(),
            new ExprKeyInConfig(),
            new ExprNewMap(),
            new ExprNewList(),
            new ExprIndexOfList(),
            new ExprKeyInMap(),
            new ExprNewType(),
            new ExprType()
        );
        registerEvents(
            new EventLoad(),
            new EventAnyLoad()
        );
        generateSyntaxFrom(IOHandlers.class);
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
    
    public LanguageElement[] getGrammar() {
        return grammar;
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
            runtime.add(this.getData(Class.forName("unsafe")));
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
    
    private Class<?>[] findClasses(final String namespace) throws IOException, ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        final CodeSource source = ScriptRunner.class.getProtectionDomain().getCodeSource();
        if (source != null) {
            final URL jar = source.getLocation();
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
    
}
