/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.mirror.ClassProvider;
import mx.kenzie.mirror.Mirror;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.compiler.SkriptCompiler;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptLoadError;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.event.Empty;
import org.byteskript.skript.runtime.event.Unload;
import org.byteskript.skript.runtime.internal.*;
import org.byteskript.skript.runtime.threading.*;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.EventData;
import org.byteskript.skript.runtime.type.OperatorFunction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Description("""
    This class is the entry-point for any program or library using ByteSkript.
    Programs looking to implement Skript can create an instance of this
    with custom (or default) compilers, thread managers, etc.
    Programs looking to interact with Skript can compile, load, unload and trigger
    events from here.
    Some resources are available in the static state, but these will relate to an
    arbitrary Skript instance that may not be running, present, etc.
    """)
public final class Skript {
    
    @Description("""
        The script thread group.
        
        If you are implementing a special thread provider that script code will use,
        it should use this group to for more accurate clean-up and profiling.
        """)
    @GenerateExample
    public static final ThreadGroup THREAD_GROUP = new ThreadGroup("skript");
    @Ignore
    public static final int JAVA_VERSION = 61;
    @Description("""
        The global variable map.
        This is obtainable through the [getter method](method:getVariables(0)).
        """)
    @GenerateExample
    static final GlobalVariableMap VARIABLES = new GlobalVariableMap();
    private static final RuntimeClassLoader LOADER = new RuntimeClassLoader(Skript.class.getClassLoader());
    private static Skript skript;
    @Ignore
    final ExecutorService executor;
    @Ignore
    final SkriptThreadProvider factory;
    @Ignore
    final ScheduledExecutorService scheduler;
    @Ignore
    final Thread mainThread;
    @Ignore
    final ModifiableCompiler compiler;
    @Ignore
    final List<OperationController> processes;
    @Ignore
    final Map<Class<? extends Event>, EventHandler> events;
    @Ignore
    final RuntimeClassLoader parent = new RuntimeClassLoader(LOADER);
    @Ignore
    final WeakList<ScriptClassLoader> loaders = new WeakList<>();
    @Ignore
    final List<Script> scripts = new ArrayList<>(); // the only strong reference, be careful!
    @Ignore
    final Map<Converter.Data, Converter<?, ?>> converters;
    @Ignore
    final Map<OperatorFunction.Data, OperatorFunction<?, ?>> operators;
    @Ignore
    private PrintStream out = System.out;
    
    @Description("""
        Create a Skript runtime with a custom (non-default) Skript compiler.
        This is used by the Jar-form script loader, which has no compiler.
        
        Some compilers may offer only partial support, such as ByteSkriptQuery's page compiler.
        
        This is different from the BridgeCompiler which is included in
        all distributions.
        
        The thread this is created from is treated as the 'main' thread.
        """)
    @GenerateExample
    public Skript(ModifiableCompiler compiler) {
        this(new SkriptThreadProvider(), compiler, Thread.currentThread());
    }
    
    @Description("""
        Create a custom Skript runtime with altered providers.
        The thread provider needs to provide ScriptThread for most features to work.
        """)
    @GenerateExample
    public Skript(SkriptThreadProvider threadProvider, ModifiableCompiler compiler, Thread main) {
        this.compiler = compiler;
        this.factory = threadProvider;
        this.factory.setSkriptInstance(this);
        this.executor = new ScriptThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            factory);
        this.mainThread = main;
        this.scheduler = new ScheduledThreadPoolExecutor(4, factory);
        this.processes = new ArrayList<>();
        this.events = new HashMap<>();
        this.converters = new HashMap<>();
        this.operators = new HashMap<>();
        skript = this;
        if (compiler != null) {
            this.converters.putAll(compiler.getConverters());
            this.operators.putAll(compiler.getOperators());
        }
    }
    
    @Description("""
        Create a default Skript runtime with all basic features present.
        The thread this is created from is treated as the 'main' thread.
        """)
    @GenerateExample
    public Skript() {
        this(new SkriptThreadProvider(), SkriptCompiler.createBasic(), Thread.currentThread());
    }
    
    @Description("""
        Gets the parent class-loader local to this thread.
        This is only usable from a script thread.
        """)
    @GenerateExample
    @ThreadSpecific
    public static RuntimeClassLoader localLoader() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) throw new ScriptRuntimeError("Not running on a script thread.");
        return thread.skript.parent;
    }
    
    @Description("""
        Finds an arbitrary parent class-loader.
        This will use the most recently-created Skript runtime.
        
        This is unsafe, since it is unlikely to be the required loader.
        """)
    @GenerateExample
    @Deprecated
    public static RuntimeClassLoader currentLoader() {
        return skript.parent;
    }
    
    @Description("""
        Attempts to find the parent class-loader.
        This will look for a local loader but default to the most-recently-created.
        This is designed for internal use.
        """)
    @GenerateExample
    @ThreadSpecific
    public static RuntimeClassLoader findLoader() {
        final Thread current = Thread.currentThread();
        if (current instanceof ScriptThread thread) return thread.skript.parent;
        return skript.parent;
    }
    
    @Description("""
        This is the map of global `{!var}` variables.
        This is a modifiable and atomic map.
        Destroying this map's contents without warning is not advised.
        
        The global map is kept in the static state so that it can be truly 'global' and runtime-independent.
        This also solves some problems with locking during atomic access.
        """)
    @GenerateExample
    public static GlobalVariableMap getVariables() {
        return VARIABLES;
    }
    
    @Description("""
        This returns the Skript instance that launched the current thread.
        This is available only to scripts.
        """)
    @GenerateExample
    @ThreadSpecific
    public static Skript localInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) throw new ScriptRuntimeError("Not running on a script thread.");
        return thread.skript;
    }
    
    private static String createClassName(String name, String path) {
        final int index = name.lastIndexOf('.');
        if (path.startsWith(File.separator)) path = path.substring(1);
        if (index == -1) return path.replace(File.separatorChar, '.');
        return path.substring(0, index).replace(File.separatorChar, '.');
    }
    
    private static List<File> getFiles(List<File> files, Path root) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (final Path path : stream) {
                if (path.toFile().isDirectory()) getFiles(files, path);
                else {
                    if (!path.toFile().getName().endsWith(".bsk")) continue;
                    files.add(path.toAbsolutePath().toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
    
    @Deprecated
    public static Class<?> findAnyClass(String name) {
        {
            final Class<?> found = getClass(name, Skript.class);
            if (found != null) return found;
        }
        final Skript skript = findInstance();
        {
            final Class<?> found = skript.getClass(name);
            if (found != null) return found;
        }
        {
            for (final Script script : skript.scripts) {
                for (final Class<?> type : script.classes()) {
                    if (type.getName().equals(name)) return type;
                }
            }
            for (final Script script : skript.scripts) {
                for (final Class<?> type : script.classes()) {
                    if (type.getSimpleName().equals(name)) return type;
                }
            }
        }
        return null;
    }
    
    private static Class<?> getClass(String name, Class<?> owner) {
        try {
            return Class.forName(name, false, owner.getClassLoader());
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    private static Skript findInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) return currentInstance();
        return thread.skript;
    }
    
    @Description("""
        This searches the app class loader and all library class loaders for the given class.
        This does not search script class loaders.
        """)
    @GenerateExample
    public Class<?> getClass(String name) {
        final Class<?> found = getClass(name, Skript.class);
        if (found != null) return found;
        for (Library library : compiler.getLibraries()) {
            final Class<?> test = getClass(name, library.getClass());
            if (test != null) return test;
        }
        return null;
    }
    
    @Description("""
        This returns the most recently-created Skript runtime.
        It is designed to be an entry-point for programs that attach in an unusual way, and have
        no other way of getting the current Skript instance.
        
        Multiple or zero runtimes may exist - this should not be depended upon.
        """)
    @Deprecated
    @GenerateExample
    public static Skript currentInstance() {
        return skript;
    }
    
    /**
     * A utility method to handle converting types for syntax.
     * This will throw an error if conversion is impossible.
     */
    public static <To> To convert(Object from, Class<To> to) {
        return convert(from, to, true);
    }
    
    /**
     * A utility method to handle converting types for syntax.
     * If the fail parameter is true, this will throw an error, otherwise returning null.
     */
    @SuppressWarnings("unchecked")
    public static <From, To> To convert(From from, Class<To> to, boolean fail) {
        try {
            if (to.isInstance(from)) return to.cast(from);
            final Skript instance = findInstance();
            final Converter<From, To> converter = (Converter<From, To>) instance.getConverter(from.getClass(), to);
            if (converter != null) return converter.convert(from);
            if (fail) throw new ScriptRuntimeError("Unable convert '" + from + "' to type " + to.getSimpleName() + ".");
            else return null;
        } catch (Throwable ex) {
            if (fail)
                throw new ScriptRuntimeError("Unable convert '" + from + "' to type " + to.getSimpleName() + ".", ex);
            else return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public <From, To> Converter<From, To> getConverter(Class<From> from, Class<To> to) {
        final Converter.Data data = new Converter.Data(from, to);
        if (converters.containsKey(data)) return (Converter<From, To>) converters.get(data);
        for (final Converter.Data found : converters.keySet()) {
            if (found.from().isAssignableFrom(from) && found.to().isAssignableFrom(to))
                return (Converter<From, To>) converters.get(found);
        }
        return null;
    }
    
    public <First, Second> OperatorFunction<?, ?> getOperatorFunction(OperatorFunction.Type type,
                                                                      Class<First> first,
                                                                      Class<Second> second) {
        final OperatorFunction.Data data = new OperatorFunction.Data(type, first, second);
        if (operators.containsKey(data)) return operators.get(data);
        for (final OperatorFunction.Data found : operators.keySet()) {
            if (found.first().isAssignableFrom(first) && found.second().isAssignableFrom(second))
                return operators.get(found);
        }
        for (final OperatorFunction.Data found : operators.keySet()) { // reverse
            if (!found.second().isAssignableFrom(first)) continue;
            if (!found.first().isAssignableFrom(second)) continue;
            final OperatorFunction<?, ?> function = operators.get(found);
            if (function.reversible()) return function;
            return function.opposite();
        }
        return null;
    }
    
    @Description("""
        Gets the parent class-loader attached to this Skript runtime.
        This is used to search available libraries and scripts for classes.
        """)
    @GenerateExample
    public RuntimeClassLoader getLoader() {
        return parent;
    }
    
    @Description("""
        Submits this instruction to a background thread.
        Background threads are safe for blocking.
        """)
    @GenerateExample
    public void runOnAsyncThread(final Instruction<?> runnable) {
        executor.submit(runnable::runSafely);
    }
    
    @Description("""
        Submits this future to a background thread.
        Background threads are safe for blocking.
        """)
    @GenerateExample
    public Future<?> getOnAsyncThread(final Instruction<?> runnable) {
        return executor.submit(runnable::get);
    }
    
    @Description("""
        Submits this instruction to a background thread.
        Background threads are safe for blocking.
        """)
    @GenerateExample
    public void runOnAsyncThread(final Runnable runnable) {
        executor.submit(runnable);
    }
    
    @Description("""
        Gets a copy of the handles for all loaded scripts.
        Storing a strong reference to these will prevent them being unloaded safely.
        These can be graveyarded (annulled in memory) without warning.
        
        This is designed for looping and discarding.
        """)
    @GenerateExample
    public Script[] getScripts() {
        return scripts.toArray(new Script[0]);
    }
    
    @Description("""
        A collection of the operation controllers for script processes.
        These should not be stored unless necessary - they hold a strong reference to the Skript runtime
        which could prevent garbage collection.
        """)
    @GenerateExample
    public Collection<OperationController> getProcesses() {
        return processes;
    }
    
    @Description("""
        Runs a runnable on a script thread.
        This is designed for running anonymous script chunks.
        
        This can also be used to run code that did not originate from a script as though it did.
        """)
    @GenerateExample
    public Future<?> runScript(final Runnable executable) {
        final OperationController controller = new OperationController(skript, factory);
        final ScriptFinishFuture future = new ScriptFinishFuture(this);
        final Runnable runnable = () -> {
            final ScriptThread thread = (ScriptThread) Thread.currentThread();
            future.thread = thread;
            thread.variables.clear();
            thread.initiator = null;
            thread.event = new Empty();
            try {
                executable.run();
            } catch (ThreadDeath ignore) {
                // This is likely from an exit the current process effect, we don't want to make noise
            } finally {
                future.finish();
            }
        };
        this.factory.newThread(controller, runnable, true).start();
        return future;
    }
    
    @Description("""
        Runs a script with a completing future.
        This is designed for use in places like JUnit tests that require throttling.
        
        This is not designed for throttling the main thread, since the airlock queue will already do this!
        """)
    @GenerateExample
    public Future<?> runScript(final ScriptRunner runner) {
        return this.runScript(runner, null);
    }
    
    @Description("""
        Runs a script with a completing future.
        This is designed for use in places like JUnit tests that require throttling.
        
        This is not designed for throttling the main thread, since the airlock queue will already do this!
        """)
    @GenerateExample
    public Future<?> runScript(final ScriptRunner runner, final Event event) {
        final OperationController controller = new OperationController(skript, factory);
        final ScriptFinishFuture future = new ScriptFinishFuture(this);
        final Runnable runnable = () -> {
            final ScriptThread thread = (ScriptThread) Thread.currentThread();
            future.thread = thread;
            thread.variables.clear(); // Some threads get regurgitated and will have shadow variables from their previous run.
            thread.initiator = runner.owner();
            thread.event = event;
            try {
                runner.run();
            } catch (ThreadDeath ignore) {
                // This is likely from an exit the current process effect, we don't want to make noise.
            } finally {
                future.value(runner.result());
                future.finish();
            }
        };
        this.factory.newThread(controller, runnable, true).start();
        return future;
    }
    
    @Description("""
        Trigger all event handlers that can deal with this event.
        Each handler will spawn its own process.
        This will trigger only the given script.
        """)
    @GenerateExample
    public EventData<?> runEvent(final Event event, final Script script) {
        boolean run = false;
        final List<ScriptFinishFuture> futures = new ArrayList<>();
        for (Map.Entry<Class<? extends Event>, EventHandler> entry : events.entrySet()) {
            final Class<? extends Event> key = entry.getKey();
            if (!key.isAssignableFrom(event.getClass())) continue;
            run = true;
            futures.addAll(Arrays.asList(entry.getValue().run(this, event, script)));
        }
        return new EventData<>(run, event, futures.toArray(new ScriptFinishFuture[0]));
    }
    
    @Description("""
        Registers an event handler for a particular event.
        Internally, this is used only during script loading.
        It has been made available in case a program wants to be notified about a Skript event.
        """)
    @GenerateExample
    public void registerEventHandler(final Class<? extends Event> event, final ScriptRunner runner) {
        this.events.putIfAbsent(event, new EventHandler());
        this.events.get(event).add(runner);
    }
    
    @Description("""
        Registers a single class library, typically compiled from a script to load
        custom syntax.
        Classes in this loader do not need to be reached post-loading - the script that provides
        the actual implementation for the syntax will be used instead.
        """)
    @GenerateExample
    public void registerLibraryClass(byte[] bytecode) throws IOException {
        final String name = getClassName(new ByteArrayInputStream(bytecode));
        final Class<?> source = new ClassLoader(Skript.class.getClassLoader()) {
            public Class<?> defineClass(String name, byte[] bytes) {
                return defineClass(name, bytes, 0, bytes.length);
            }
        }.defineClass(name, bytecode);
        final ModifiableLibrary library = new ModifiableLibrary(name);
        library.generateSyntaxFrom(source);
        compiler.addLibrary(library);
    }
    
    private static String getClassName(InputStream input)
        throws IOException {
        final DataInputStream stream = new DataInputStream(input);
        stream.readLong();
        final int paths = (stream.readShort() & 0xffff) - 1;
        final int[] classes = new int[paths];
        final String[] names = new String[paths];
        for (int i = 0; i < paths; i++) {
            final int t = stream.read();
            if (t == 7) classes[i] = stream.readShort() & 0xffff;
            else if (t == 1) names[i] = stream.readUTF();
            else if (t == 5 || t == 6) {
                stream.readLong();
                i++;
            } else if (t == 8) stream.readShort();
            else stream.readInt();
        }
        stream.readShort();
        return names[classes[(stream.readShort() & 0xffff) - 1] - 1].replace('/', '.');
    }
    
    @Description("""
        Registers a library instance to the current compiler.
        """)
    @GenerateExample
    @SuppressWarnings("unchecked")
    public boolean registerLibrary(Library library) {
        for (final Map.Entry<Converter.Data, Converter<?, ?>> entry : library.getConverters().entrySet()) {
            final Converter.Data data = entry.getKey();
            this.registerConverter((Class<Object>) data.from(), (Class<Object>) data.to(), (Converter<Object, Object>) entry.getValue());
        }
        return compiler.addLibrary(library);
    }
    
    public <From, To> void registerConverter(Class<From> from, Class<To> to, Converter<From, To> converter) {
        final Converter.Data data = new Converter.Data(from, to);
        this.converters.put(data, converter);
    }
    
    @Description("""
        Removes a library instance from the current compiler.
        """)
    @GenerateExample
    public boolean unregisterLibrary(Library library) {
        for (final Map.Entry<Converter.Data, Converter<?, ?>> entry : library.getConverters().entrySet()) {
            final Converter.Data data = entry.getKey();
            this.unregisterConverter(data.from(), data.to());
        }
        return compiler.removeLibrary(library);
    }
    
    public <From, To> void unregisterConverter(Class<From> from, Class<To> to) {
        final Converter.Data data = new Converter.Data(from, to);
        this.converters.remove(data);
    }
    
    @Description("""
        Returns the provided script compiler.
        """)
    @GenerateExample
    public ModifiableCompiler getCompiler() {
        return compiler;
    }
    
    @Description("""
        Returns an array of all registered libraries.
        """)
    @GenerateExample
    public Library[] getLoadedLibraries() {
        if (!this.hasCompiler()) return new Library[0];
        return compiler.getLibraries();
    }
    
    @Description("""
        Whether this Skript runtime has a compiler.
        """)
    @GenerateExample
    public boolean hasCompiler() {
        return compiler != null;
    }
    
    @Description("""
        Compiles all scripts in the root directory to the output directory.
        This is used by the 'compile' goal.
        This is designed for use by compiler apps rather than programs.
        """)
    @GenerateExample
    @CompilerDependent
    public Collection<File> compileScripts(final File root, final File outputDirectory)
        throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<File> outputs = new ArrayList<>();
        for (final File file : files)
            try (final InputStream input = new FileInputStream(file)) {
                final String name = this.getClassName(file, root);
                outputs.addAll(compileComplexScript(input, name, outputDirectory));
            }
        return outputs;
    }
    
    @Description("""
        Compiles a source input stream and writes it to a target output stream.
        This can compile only simple scripts (which create a single class file.)
        """)
    @GenerateExample
    @CompilerDependent
    public void compileScript(final InputStream input, final String name, final OutputStream target)
        throws IOException {
        final PostCompileClass datum = this.compileScript(input, name);
        target.write(datum.code());
    }
    
    @Description("""
        Compiles a simple script from its source to a memory class.
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public PostCompileClass compileScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        return classes[0];
    }
    
    @Description("""
        Compiles a complex script to multiple class files in the output directory.
        """)
    @GenerateExample
    @CompilerDependent
    public Collection<File> compileComplexScript(final InputStream input, final String name, final File outputDirectory)
        throws IOException {
        if (!outputDirectory.exists()) throw new ScriptLoadError("Output folder does not exist.");
        if (!outputDirectory.isDirectory()) throw new ScriptLoadError("Output must be a folder.");
        final PostCompileClass[] data = compileComplexScript(input, name);
        final List<File> outputs = new ArrayList<>();
        for (PostCompileClass datum : data) {
            final File target = new File(outputDirectory, datum.name() + ".class");
            try (OutputStream stream = new FileOutputStream(target)) {
                stream.write(datum.code());
            }
            outputs.add(target);
        }
        return outputs;
    }
    
    @Description("""
        Compiles all scripts in the root file to code representations.
        These representations may be loaded, written to files or otherwise used.
        """)
    @GenerateExample
    @CompilerDependent
    public PostCompileClass[] compileScripts(final File root) throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<PostCompileClass> scripts = new ArrayList<>();
        for (final File file : files) {
            if (file == null) continue;
            if (!file.getName().endsWith(".bsk")) continue;
            try (final InputStream stream = new FileInputStream(file)) {
                final String name = this.getClassName(file, root);
                scripts.addAll(Arrays.asList(this.compileComplexScript(stream, name)));
            }
        }
        return scripts.toArray(new PostCompileClass[0]);
    }
    
    @Description("""
        Compiles a single script to its class files.
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public PostCompileClass[] compileComplexScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        return classes;
    }
    
    @Description("""
        Compiles a simple script from its string source to a memory class.
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public PostCompileClass compileScript(final String code, final String name) {
        return this.compileScript(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name);
    }
    
    @Description("""
        Compiles a simple script from its string source to a memory class.
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public Promise<PostCompileClass[]> compileScriptAsync(final String code, final String name) {
        return this.compileComplexScriptAsync(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name);
    }
    
    @Description("""
        Compiles a single script to its class files.
        This version runs in the background and returns a promise
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public Promise<PostCompileClass[]> compileComplexScriptAsync(final InputStream stream, final String name) {
        return compiler.compileAsync(stream, new Type(name), this);
    }
    
    @Description("""
        Unloads a script by its main class.
        This is a destructive operation.
        """)
    @GenerateExample
    public void unloadScript(Class<?> main) {
        for (final Script script : scripts.toArray(new Script[0]))
            if (script.mainClass() == main) this.unloadScript(script);
    }
    
    @Description("""
        Unloads a script. This is a destructive operation.
        All metadata for the script will be 'graveyarded' - its memory address will be erased.
        Anything keeping a reference to the script will prevent it being garbage-collected.
        """)
    @GenerateExample
    public void unloadScript(Script script) {
        final Unload unload = new Unload(script);
        script.stop();
        this.loaders.removeIf(ref -> ref.refersTo((ScriptClassLoader) script.mainClass().getClassLoader()));
        this.runEvent(unload);
        synchronized (events) {
            final List<Class<? extends Event>> toRemove = new ArrayList<>();
            for (final Map.Entry<Class<? extends Event>, EventHandler> entry : events.entrySet()) {
                final EventHandler value = entry.getValue();
                for (final ScriptRunner trigger : value.getTriggers().toArray(new ScriptRunner[0])) {
                    if (trigger.owner() != script.mainClass()) continue;
                    value.getTriggers().remove(trigger);
                    UnsafeAccessor.graveyard(trigger);
                    toRemove.add(entry.getKey());
                }
            }
            for (final Class<? extends Event> clazz : toRemove) events.remove(clazz);
        }
        this.scripts.remove(script);
        UnsafeAccessor.graveyard(script);
    }
    
    @Description("""
        Trigger all event handlers that can deal with this event.
        Each handler will spawn its own process.
        """)
    @GenerateExample
    public EventData<?> runEvent(final Event event) {
        boolean run = false;
        final List<ScriptFinishFuture> futures = new ArrayList<>();
        for (Map.Entry<Class<? extends Event>, EventHandler> entry : events.entrySet()) {
            final Class<? extends Event> key = entry.getKey();
            if (!key.isAssignableFrom(event.getClass())) continue;
            run = true;
            futures.addAll(Arrays.asList(entry.getValue().run(this, event)));
        }
        return new EventData<>(run, event, futures.toArray(new ScriptFinishFuture[0]));
    }
    
    @Description("""
        This is designed for internal use.
        """)
    @Deprecated
    @CompilerDependent
    public Script compileLoad(File file, String name) throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
            return this.compileLoad(stream, name);
        }
    }
    
    @Description("""
        This is designed for internal use.
        """)
    @Deprecated
    @CompilerDependent
    public Script compileLoad(InputStream stream, String name) {
        return this.loadScript(compileScript(stream, name));
    }
    
    @Description("""
        Loads a script from compiled source code.
        """)
    @GenerateExample
    public Script loadScript(final PostCompileClass datum) {
        return this.loadScript(this.loadClass(datum.name(), datum.code()));
    }
    
    @Description("""
        Loads a script from a defined class.
        """)
    @GenerateExample
    public Script loadScript(final Class<?> loaded) {
        final Script script = new Script(this, null, loaded);
        this.scripts.add(script);
        return script;
    }
    
    private Class<?> loadClass(String name, byte[] bytecode) {
        return this.createLoader().loadClass(name, bytecode);
    }
    
    private SkriptMirror createLoader() {
        final ScriptClassLoader loader = new ScriptClassLoader();
        this.loaders.addActual(loader);
        return new SkriptMirror(loader);
    }
    
    @Description("""
        Loads a script from compiled source code.
        """)
    @GenerateExample
    public Script loadScript(final PostCompileClass[] data) {
        final Class<?>[] classes = new Class[data.length];
        final SkriptMirror loader = createLoader();
        for (int i = 0; i < data.length; i++) classes[i] = loader.loadClass(data[i].name(), data[i].code());
        return this.loadScript(classes);
    }
    
    @Description("""
        Loads a script from defined classes.
        """)
    @GenerateExample
    public Script loadScript(final Class<?>[] loaded) {
        final Script script = new Script(this, null, loaded);
        this.scripts.add(script);
        return script;
    }
    
    public Script getScript(final Class<?> part) {
        for (final Script script : this.scripts) if (script.ownsClass(part)) return script;
        return null;
    }
    
    @Description("""
        Loads all compiled classes from the given root file as scripts.
        This is not a safe operation - not all compiled classes are suitable to be loaded in this way.
        """)
    @GenerateExample
    @Deprecated
    public Collection<Script> loadScripts(final File root) throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<Script> scripts = new ArrayList<>();
        for (final File file : files) {
            if (!file.getName().endsWith(".class")) continue;
            if (file.isDirectory()) continue;
            try (final InputStream namer = new FileInputStream(file);
                 final InputStream stream = new FileInputStream(file)) {
                final String name = getClassName(namer);
                scripts.add(loadScript(stream, name));
            }
        }
        return scripts;
    }
    
    @Description("""
        Compiles and loads all scripts from a source directory.
        This method may be unavailable in some distributions.
        """)
    @GenerateExample
    @CompilerDependent
    public Collection<Script> compileLoadScripts(final File root) throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<Script> scripts = new ArrayList<>();
        for (final File file : files) {
            if (!file.getName().endsWith(".bsk")) continue;
            if (file.isDirectory()) continue;
            try (InputStream stream = new FileInputStream(file)) {
                final String name = getClassName(file, root);
                scripts.add(loadScript(compileScript(stream, name)));
            }
        }
        return scripts;
    }
    
    private String getClassName(final File file, final File root) {
        final String source = root.getName();
        String path = source + "." + file.getAbsolutePath().replace(root.getAbsolutePath() + File.separatorChar, "");
        path = path.replace(File.separatorChar, '.');
        path = path.substring(0, path.lastIndexOf('.'));
        return path;
    }
    
    @Description("""
        Assembles a script in memory without loading it.
        It is not safe to assume this script will never be triggered or loaded.
        This registers the event handlers, which will be triggered if an event occurs in the meantime.
        """)
    @GenerateExample
    public Script assembleScript(final PostCompileClass... data) {
        if (data.length == 0) return null;
        final List<Class<?>> classes = new ArrayList<>();
        for (PostCompileClass datum : data) {
            final Class<?> part = this.createLoader().loadClass(datum.name(), datum.code());
            classes.add(part);
        }
        return this.assembleScript(classes.toArray(new Class[0]));
    }
    
    @Description("""
        Assembles a script from defined classes.
        """)
    @GenerateExample
    public Script assembleScript(final Class<?>... loaded) {
        final Script script = new Script(false, this, null, loaded);
        this.scripts.add(script);
        return script;
    }
    
    @Description("""
        Loads scripts from compiled source code.
        """)
    @GenerateExample
    public Collection<Script> loadScripts(final PostCompileClass[] data) {
        final List<Script> classes = new ArrayList<>();
        for (PostCompileClass datum : data) classes.add(this.loadScript(datum));
        return classes;
    }
    
    @Description("""
        Loads a script from a stream of its compiled bytecode.
        """)
    @GenerateExample
    public Script loadScript(final InputStream stream)
        throws IOException {
        return loadScript(stream.readAllBytes());
    }
    
    @Description("""
        Loads a script from its compiled bytecode.
        The name is determined by reading the class name entry from the code.
        """)
    @GenerateExample
    public Script loadScript(final byte[] bytecode) throws IOException {
        return loadScript(bytecode, getClassName(new ByteArrayInputStream(bytecode)));
    }
    
    @Description("""
        Loads a script from its compiled bytecode and class name.
        """)
    @GenerateExample
    public Script loadScript(final byte[] bytecode, final String name) {
        return loadScript(this.loadClass(name, bytecode));
    }
    
    @Description("""
        Loads a script from its compiled class file.
        """)
    @GenerateExample
    public Script loadScript(final File source)
        throws IOException {
        try (final InputStream stream = new FileInputStream(source)) {
            return loadScript(stream, source.getName());
        }
    }
    
    @Description("""
        Loads a script from a stream of its compiled bytecode.
        """)
    @GenerateExample
    public Script loadScript(final InputStream stream, final String name)
        throws IOException {
        return loadScript(this.loadClass(name, stream.readAllBytes()));
    }
    
    @Description("""
        Loads a script from its compiled class file.
        """)
    @GenerateExample
    public Script loadScript(final File source, final String name)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return loadScript(this.loadClass(name, stream.readAllBytes()));
        }
    }
    
    //region Output
    
    /**
     * Set the current print stream used by the `print` effect.
     * This can be used to redirect output in a particular state.
     */
    public void setOutput(final PrintStream out) {
        if (out == null) this.out = System.out;
        else this.out = out;
    }
    
    public void println(Object object) {
        this.out.println(object);
    }
    
    public void print(Object object) {
        this.out.print(object);
    }
    //endregion
    
    @Description("""
        Generates a script thread from the given process.
        """)
    @GenerateExample
    public Thread allocateProcess(final OperationController controller, final Runnable runnable, boolean inheritLocals) {
        return factory.newThread(controller, runnable, inheritLocals);
    }
    
    @Description("""
        The 'main' thread this runtime was given.
        This is used mostly for locking.
        """)
    public Thread getMainThread() {
        return mainThread;
    }
    
    @Description("""
        The scheduler service this runtime uses.
        """)
    @GenerateExample
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
    
    @Description("""
        The executor service this runtime uses.
        This is separate from the scheduler so that threads can be re-used more efficiently.
        """)
    @GenerateExample
    public ExecutorService getExecutor() {
        return executor;
    }
    
    @Description("""
        Schedules a task to be run at some point in the future.
        """)
    @GenerateExample
    public Future<?> schedule(Runnable runnable, long millis) {
        return scheduler.schedule(runnable, millis, TimeUnit.MILLISECONDS);
    }
    
    @Description("""
        This class handles the class-loading delegation for libraries and scripts.
        It should not be interacted with directly, see [getLoader](method:getLoader(0)) for an instance.
        """)
    @GenerateExample
    public static class RuntimeClassLoader extends ClassLoader implements ClassProvider {
        @Ignore
        protected RuntimeClassLoader(ClassLoader parent) {
            super(parent);
        }
        
        @Override
        @Ignore
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException ex) {
                for (final ScriptClassLoader value : Skript.findInstance().loaders.collectRemaining()) {
                    if (value == null) continue;
                    try {
                        return value.loadClass0(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                throw ex;
            }
        }
        
        @Override
        @Ignore
        public Class<?> findClass(String name) {
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                for (final ScriptClassLoader value : Skript.findInstance().loaders.collectRemaining()) {
                    if (value == null) continue;
                    try {
                        return value.findClass0(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                return Skript.findInstance().getClass(name);
            }
        }
        
        @Ignore
        public Class<?> loadClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
        
        @Override
        @Ignore
        public Class<?> loadClass(Class<?> aClass, String name, byte[] bytecode) {
            try {
                return Class.forName(name, false, this);
            } catch (ClassNotFoundException ex) {
                return super.defineClass(name, bytecode, 0, bytecode.length);
            }
        }
    }
    
    @Description("""
        This mirror handles class injection.
        This should not be interacted with by an external program.
        """)
    static class SkriptMirror extends Mirror<Object> {
        
        @GenerateExample
        protected SkriptMirror(ClassProvider provider) {
            super(Skript.class);
            useProvider(provider);
        }
        
        @Override
        @Ignore
        public Class<?> loadClass(String name, byte[] bytecode) {
            return super.loadClass(name, bytecode);
        }
    }
    
    public class Test {
        private final PrintStream out;
        private final List<Throwable> errors = new ArrayList<>();
        private final boolean write;
        private int failure = 0;
        
        public Test() {
            this(false, System.out);
        }
        
        public Test(boolean writeClasses, PrintStream out) {
            this.write = writeClasses;
            this.out = out;
        }
        
        public Test(boolean writeClasses) {
            this(writeClasses, System.out);
        }
        
        public Test(PrintStream out) {
            this(false, out);
        }
        
        public void testDirectory(Path folder) {
            try (final Stream<Path> stream = Files.walk(folder, 1)) {
                final Iterator<Path> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    final Path file = iterator.next();
                    if (!file.toString().endsWith(".bsk")) continue;
                    this.test(file);
                }
            } catch (IOException ex) {
                this.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to run.");
                this.errors.add(ex);
                this.failure++;
            }
        }
        
        public void test(Path file) {
            final String part = file.toString().substring(file.toString().indexOf("/tests/") + 7);
            final String name = part.substring(0, part.length() - 4).replace(File.separatorChar, '.');
            System.setProperty("skript.test_mode", "true");
            this.println(ConsoleColour.RESET + "Running test '" + ConsoleColour.GREEN + name + ConsoleColour.RESET + "':");
            try (final InputStream stream = Files.newInputStream(file)) {
                final PostCompileClass[] classes;
                synchronized (this) {
                    try {
                        final long now, then;
                        now = System.currentTimeMillis();
                        classes = Skript.this.compileComplexScript(stream, "skript." + name);
                        then = System.currentTimeMillis();
                        this.println(ConsoleColour.GREEN + "\t✓ " + ConsoleColour.RESET + "Parsed in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                    } catch (Throwable ex) {
                        this.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to parse.");
                        this.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to run.");
                        this.errors.add(ex);
                        this.failure++;
                        return;
                    }
                    if (write) {
                        final File test = new File("target/test-scripts/" + classes[0].name() + ".class");
                        test.getParentFile().mkdirs();
                        if (!test.exists()) test.createNewFile();
                        try (final OutputStream output = new FileOutputStream(test)) {
                            output.write(classes[0].code());
                        }
                    }
                    try {
                        final long now, then;
                        final Script script = Skript.this.loadScript(classes);
                        now = System.currentTimeMillis();
                        final boolean result;
                        final Object object = script.getFunction("test").run(Skript.this).get();
                        result = Boolean.TRUE.equals(object);
                        then = System.currentTimeMillis();
                        if (result)
                            this.println(ConsoleColour.GREEN + "\t✓ " + ConsoleColour.RESET + "Run in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                        else {
                            this.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Run in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                            this.failure++;
                        }
                    } catch (Throwable ex) {
                        this.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to run.");
                        this.errors.add(ex);
                        this.failure++;
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            System.setProperty("skript.test_mode", "");
        }
        
        protected void println(Object value) {
            if (out != null) out.println(value);
        }
        
        public List<Throwable> getErrors() {
            return errors;
        }
        
        public int getFailureCount() {
            return failure;
        }
    }
    
}
