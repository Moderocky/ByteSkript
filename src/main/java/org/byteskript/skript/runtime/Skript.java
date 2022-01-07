/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

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
import org.byteskript.skript.runtime.internal.*;
import org.byteskript.skript.runtime.threading.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class is the entry-point for any program or library using ByteSkript.
 * Programs looking to implement Skript can create an instance of this
 * with custom (or default) compilers, thread managers, etc.
 * Programs looking to interact with Skript can compile, load, unload and trigger
 * events from here.
 * Some resources are available in the static state, but these will relate to an
 * arbitrary Skript instance that may not be running, present, etc.
 */
public final class Skript {
    
    public static final ThreadGroup THREAD_GROUP = new ThreadGroup("skript");
    public static final int JAVA_VERSION = 61;
    static final GlobalVariableMap VARIABLES = new GlobalVariableMap();
    private static final RuntimeClassLoader LOADER = new RuntimeClassLoader(Skript.class.getClassLoader());
    private static Skript skript;
    final ExecutorService executor;
    final SkriptThreadProvider factory;
    final ScheduledExecutorService scheduler;
    final Thread mainThread;
    final ModifiableCompiler compiler;
    final List<OperationController> processes;
    final Map<Class<? extends Event>, EventHandler> events;
    final RuntimeClassLoader parent = new RuntimeClassLoader(LOADER);
    final WeakList<ScriptClassLoader> loaders = new WeakList<>();
    final List<Script> scripts = new ArrayList<>(); // the only strong reference, be careful!
    
    /**
     * Create a Skript runtime with a custom (non-default) Skript compiler.
     * This is used by the Jar-form script loader, which has no compiler.
     * Some compilers may offer only partial support, such as ByteSkriptQuery's page compiler.
     * <p>
     * This is different from the {@link org.byteskript.skript.compiler.BridgeCompiler} which is included in
     * all distributions.
     * <p>
     * The thread this is created from is treated as the 'main' thread.
     *
     * @param compiler potentially null script compiler
     */
    public Skript(ModifiableCompiler compiler) {
        this(new SkriptThreadProvider(), compiler, Thread.currentThread());
    }
    
    /**
     * Create a custom Skript runtime with altered providers.
     * The thread provider needs to provide {@link ScriptThread}s for most features to work.
     *
     * @param threadProvider the thread provider
     * @param compiler       the compiler
     * @param main           the 'main' thread for locking
     */
    public Skript(SkriptThreadProvider threadProvider, ModifiableCompiler compiler, Thread main) {
        this.compiler = compiler;
        this.factory = threadProvider;
        this.factory.setSkriptInstance(this);
        executor = Executors.newCachedThreadPool(factory);
        this.mainThread = main;
        this.scheduler = new ScheduledThreadPoolExecutor(4, factory);
        this.processes = new ArrayList<>();
        this.events = new HashMap<>();
        skript = this;
    }
    
    /**
     * Create a default Skript runtime with all basic features present.
     * The thread this is created from is treated as the 'main' thread.
     */
    public Skript() {
        this(new SkriptThreadProvider(), SkriptCompiler.createBasic(), Thread.currentThread());
    }
    
    /**
     * Gets the parent class-loader local to this thread.
     * This is only usable from a script thread.
     *
     * @return the local runtime loader
     */
    @ThreadSpecific
    public static RuntimeClassLoader localLoader() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Not running on a script thread.");
        return thread.skript.parent;
    }
    
    /**
     * Finds an arbitrary parent class-loader.
     * This will use the most recently-created Skript runtime.
     * This is unsafe, since it is unlikely to be the required loader.
     *
     * @return potentially null class-loader
     */
    @Deprecated
    public static RuntimeClassLoader currentLoader() {
        return skript.parent;
    }
    
    /**
     * Attempts to find the parent class-loader.
     * This will look for a local loader but default to the most-recently-created.
     * This is designed for internal use.
     *
     * @return an arbitrary class-loader
     */
    @ThreadSpecific
    public static RuntimeClassLoader findLoader() {
        final Thread current = Thread.currentThread();
        if (current instanceof ScriptThread thread)
            return thread.skript.parent;
        return skript.parent;
    }
    
    /**
     * This is the map of global `{!var}` variables.
     * This is a modifiable and atomic map.
     * Destroying this map's contents without warning is not advised.
     * <p>
     * The global map is kept in the static state so that it can be truly 'global' and runtime-independent.
     * This also solves some problems with locking during atomic access.
     *
     * @return the global variable map
     */
    public static GlobalVariableMap getVariables() {
        return VARIABLES;
    }
    
    /**
     * This returns the Skript instance that launched the current thread.
     * This is available only to scripts.
     *
     * @return the launching instance
     */
    @ThreadSpecific
    public static Skript localInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread))
            throw new ScriptRuntimeError("Not running on a script thread.");
        return thread.skript;
    }
    
    private static Skript findInstance() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof ScriptThread thread)) return currentInstance();
        return thread.skript;
    }
    
    /**
     * This returns the most recently-created Skript runtime.
     * It is designed to be an entry-point for programs that attach in an unusual way, and have
     * no other way of getting the current Skript instance.
     * <p>
     * Multiple or zero runtimes may exist - this should not be depended upon.
     *
     * @return potentially null Skript runtime
     */
    @Deprecated
    public static Skript currentInstance() {
        return skript;
    }
    
    private static String createClassName(String name, String path) {
        final int index = name.lastIndexOf('.');
        if (path.startsWith(File.separator)) path = path.substring(1);
        if (index == -1) return path.replace(File.separatorChar, '.');
        return path.substring(0, index).replace(File.separatorChar, '.');
    }
    
    private static List<File> getFiles(List<File> files, Path root) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFiles(files, path);
                } else {
                    if (!path.toFile().getName().endsWith(".bsk")) continue;
                    files.add(path.toAbsolutePath().toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
    
    /**
     * Gets the parent class-loader attached to this Skript runtime.
     * This is used to search available libraries and scripts for classes.
     *
     * @return this runtime loader
     */
    public RuntimeClassLoader getLoader() {
        return parent;
    }
    
    /**
     * Submits this instruction to a background thread.
     * Background threads are safe for blocking.
     *
     * @param runnable the instruction
     */
    public void runOnAsyncThread(final Instruction<?> runnable) {
        executor.submit(runnable::runSafely);
    }
    
    /**
     * Submits this future to a background thread.
     * Background threads are safe for blocking.
     *
     * @param runnable the task
     * @return a future
     */
    public Future<?> getOnAsyncThread(final Instruction<?> runnable) {
        return executor.submit(runnable::get);
    }
    
    /**
     * Submits this instruction to a background thread.
     * Background threads are safe for blocking.
     *
     * @param runnable the instruction
     */
    public void runOnAsyncThread(final Runnable runnable) {
        executor.submit(runnable);
    }
    
    /**
     * Gets a copy of the handles for all loaded scripts.
     * Storing a strong reference to these will prevent them being unloaded safely.
     * These can be graveyarded (annulled in memory) without warning.
     * <p>
     * This is designed for looping and discarding.
     *
     * @return the scripts owned by this instance
     */
    public Script[] getScripts() {
        return scripts.toArray(new Script[0]);
    }
    
    /**
     * A collection of the operation controllers for script processes.
     * These should not be stored unless necessary - they hold a strong reference to the Skript runtime
     * which could prevent garbage collection.
     *
     * @return the process queues for this runtime
     */
    public Collection<OperationController> getProcesses() {
        return processes;
    }
    
    /**
     * Runs a script with a completing future.
     * This is designed for use in places like JUnit tests that require throttling.
     * This is not designed for throttling the main thread, since the airlock queue will already do this!
     *
     * @param runner the running task
     * @return a future for the script's halting completion
     */
    public Future<?> runScript(final ScriptRunner runner) {
        return runScript(runner, null);
    }
    
    /**
     * Runs a script with a completing future.
     * This is designed for use in places like JUnit tests that require throttling.
     * This is not designed for throttling the main thread, since the airlock queue will already do this!
     *
     * @param runner the running task
     * @param event  the event that triggered this script
     * @return a future for the script's halting completion
     */
    public Future<?> runScript(final ScriptRunner runner, final Event event) {
        final OperationController controller = new OperationController(skript, factory);
        final ScriptFinishFuture future = new ScriptFinishFuture(this);
        final Runnable runnable = () -> {
            final ScriptThread thread = (ScriptThread) Thread.currentThread();
            future.thread = thread;
            thread.variables.clear();
            thread.initiator = runner.owner();
            thread.event = event;
            try {
                runner.run();
            } catch (ThreadDeath ignore) {
                // This is likely from an exit the current process effect, we don't want to make noise
            } finally {
                future.finish();
            }
        };
        factory.newThread(controller, runnable, true).start();
        return future;
    }
    
    /**
     * Trigger all event handlers that can deal with this event.
     * Each handler will spawn its own process.
     *
     * @param event the triggering event
     * @return whether any handlers were notified
     */
    public boolean runEvent(final Event event) {
        boolean run = false;
        for (Map.Entry<Class<? extends Event>, EventHandler> entry : events.entrySet()) {
            final Class<? extends Event> key = entry.getKey();
            if (!key.isAssignableFrom(event.getClass())) continue;
            run = true;
            entry.getValue().run(this, event);
        }
        return run;
    }
    
    /**
     * Trigger all event handlers that can deal with this event.
     * Each handler will spawn its own process.
     * This will trigger only the given script.
     *
     * @param event  the triggering event
     * @param script the script for which to trigger handlers
     * @return whether any handlers were notified
     */
    public boolean runEvent(final Event event, final Script script) {
        boolean run = false;
        for (Map.Entry<Class<? extends Event>, EventHandler> entry : events.entrySet()) {
            final Class<? extends Event> key = entry.getKey();
            if (!key.isAssignableFrom(event.getClass())) continue;
            run = true;
            entry.getValue().run(this, event, script);
        }
        return run;
    }
    
    /**
     * Registers an event handler for a particular event.
     * Internally, this is used only during script loading.
     * It has been made available in case a program wants to be notified about a Skript event.
     *
     * @param event  the class of the event to watch for
     * @param runner the runner to be notified
     */
    public void registerEventHandler(final Class<? extends Event> event, final ScriptRunner runner) {
        this.events.putIfAbsent(event, new EventHandler());
        this.events.get(event).add(runner);
    }
    
    /**
     * This searches the app class loader and all library class loaders for the given class.
     * This does not search script class loaders.
     *
     * @param name the class dot-path
     * @return the class, or null
     */
    public Class<?> getClass(String name) {
        final Class<?> found = getClass(name, Skript.class);
        if (found != null) return found;
        for (Library library : compiler.getLibraries()) {
            final Class<?> test = getClass(name, library.getClass());
            if (test != null) return test;
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
    
    /**
     * Registers a single class library, typically compiled from a script to load
     * custom syntax.
     * Classes in this loader do not need to be reached post-loading - the script that provides
     * the actual implementation for the syntax will be used instead.
     *
     * @param bytecode the class bytecode
     * @throws IOException if something goes wrong
     */
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
    
    private static String getClassName(InputStream is)
        throws IOException {
        final DataInputStream stream = new DataInputStream(is);
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
    
    /**
     * Registers a library instance to the current compiler.
     *
     * @param library the library to register
     * @return whether the library was added
     */
    public boolean registerLibrary(Library library) {
        return compiler.addLibrary(library);
    }
    
    /**
     * Removes a library instance from the current compiler.
     *
     * @param library the library to remove
     * @return whether the library was removed
     */
    public boolean unregisterLibrary(Library library) {
        return compiler.removeLibrary(library);
    }
    
    /**
     * Returns the provided script compiler.
     *
     * @return potentially null compiler
     */
    public ModifiableCompiler getCompiler() {
        return compiler;
    }
    
    /**
     * Returns an array of all registered libraries.
     *
     * @return all available libraries
     */
    public Library[] getLoadedLibraries() {
        if (!this.hasCompiler()) return new Library[0];
        return compiler.getLibraries();
    }
    
    /**
     * Whether this Skript runtime has a compiler.
     *
     * @return if compiler is present
     */
    public boolean hasCompiler() {
        return compiler != null;
    }
    
    /**
     * Compiles all scripts in the root directory to the output directory.
     * This is used by the 'compile' goal.
     * This is designed for use by compiler apps rather than programs.
     *
     * @param root            the source directory
     * @param outputDirectory the target directory
     * @return a list of all compiled class files
     * @throws IOException if an error occurs
     */
    @CompilerDependent
    public Collection<File> compileScripts(final File root, final File outputDirectory)
        throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<File> outputs = new ArrayList<>();
        for (final File file : files) {
            try (final InputStream input = new FileInputStream(file)) {
                final String name = getClassName(file, root);
                outputs.addAll(compileComplexScript(input, name, outputDirectory));
            }
        }
        return outputs;
    }
    
    /**
     * Compiles a source input stream and writes it to a target output stream.
     * This can compile only simple scripts (which create a single class file.)
     *
     * @param input  the source
     * @param name   the class name
     * @param target the target
     * @throws IOException if something goes wrong
     */
    @CompilerDependent
    public void compileScript(final InputStream input, final String name, final OutputStream target)
        throws IOException {
        final PostCompileClass datum = compileScript(input, name);
        target.write(datum.code());
    }
    
    /**
     * Compiles a simple script from its source to a memory class.
     * This method may be unavailable in some distributions.
     *
     * @param stream the source
     * @param name   the class name
     * @return the 'main' class
     */
    @CompilerDependent
    public PostCompileClass compileScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        return classes[0];
    }
    
    /**
     * Compiles a complex script to multiple class files in the output directory.
     *
     * @param input           the source
     * @param name            the class name
     * @param outputDirectory the target directory
     * @return all files compiled
     * @throws IOException if something goes wrong
     */
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
    
    /**
     * Compiles all scripts in the root file to code representations.
     * These representations may be loaded, written to files or otherwise used.
     *
     * @param root the source directory
     * @return all compiled classes
     * @throws IOException if something goes wrong
     */
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
    
    /**
     * Compiles a single script to its class files.
     * This method may be unavailable in some distributions.
     *
     * @param stream the source
     * @param name   the class name
     * @return the classes produced
     */
    @CompilerDependent
    public PostCompileClass[] compileComplexScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        return classes;
    }
    
    /**
     * Compiles a simple script from its string source to a memory class.
     * This method may be unavailable in some distributions.
     *
     * @param code the source
     * @param name the class name
     * @return the 'main' class
     */
    @CompilerDependent
    public PostCompileClass compileScript(final String code, final String name) {
        return compileScript(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name);
    }
    
    /**
     * Unloads a script by its main class.
     * This is a destructive operation.
     *
     * @param main the main class
     */
    public void unloadScript(Class<?> main) {
        for (Script script : scripts.toArray(new Script[0])) {
            if (script.mainClass() == main) this.unloadScript(script);
        }
    }
    
    /**
     * Unloads a script. This is a destructive operation.
     * All metadata for the script will be 'graveyarded' - its memory address will be erased.
     * Anything keeping a reference to the script will prevent it being garbage-collected.
     *
     * @param script the script
     */
    public void unloadScript(Script script) {
        synchronized (events) {
            for (final EventHandler value : events.values()) {
                for (final ScriptRunner trigger : value.getTriggers().toArray(new ScriptRunner[0])) {
                    if (trigger.owner() != script.mainClass()) continue;
                    value.getTriggers().remove(trigger);
                    UnsafeAccessor.graveyard(trigger);
                }
            }
        }
        scripts.remove(script);
        UnsafeAccessor.graveyard(script);
    }
    
    /**
     * This is designed for internal use.
     *
     * @param file the source
     * @param name the class name
     * @return the loaded script.
     * @throws IOException if something goes wrong
     */
    @CompilerDependent
    public Script compileLoad(File file, String name) throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
            return compileLoad(stream, name);
        }
    }
    
    /**
     * This is designed for internal use.
     *
     * @param stream the source
     * @param name   the class name
     * @return the loaded script.
     */
    @CompilerDependent
    public Script compileLoad(InputStream stream, String name) {
        return loadScript(compileScript(stream, name));
    }
    
    /**
     * Loads a script from compiled source code.
     *
     * @param datum the class code
     * @return the script
     */
    public Script loadScript(final PostCompileClass datum) {
        return this.loadScript(this.loadClass(datum.name(), datum.code()));
    }
    
    /**
     * Loads a script from a defined class.
     *
     * @param loaded the class
     * @return the script
     */
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
    
    /**
     * Loads all compiled classes from the given root file as scripts.
     * This is not a safe operation - not all compiled classes are suitable to be loaded in this way.
     *
     * @param root the source root
     * @return the loaded scripts
     * @throws IOException if something goes wrong
     */
    @Deprecated
    public Collection<Script> loadScripts(final File root) throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<Script> scripts = new ArrayList<>();
        for (final File file : files) {
            if (!file.getName().endsWith(".class")) continue;
            if (file.isDirectory()) continue;
            try (InputStream namer = new FileInputStream(file); InputStream stream = new FileInputStream(file)) {
                final String name = getClassName(namer);
                scripts.add(loadScript(stream, name));
            }
        }
        return scripts;
    }
    
    /**
     * Compiles and loads all scripts from a source directory.
     * This method may be unavailable in some distributions.
     *
     * @param root source directory
     * @return a collection of loaded scripts
     * @throws IOException if something goes wrong
     */
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
    
    /**
     * Assembles a script in memory without loading it.
     * It is not safe to assume this script will never be triggered or loaded.
     * This registers the event handlers, which will be triggered if an event occurs in the meantime.
     *
     * @param data the script data
     * @return the script
     */
    public Script assembleScript(final PostCompileClass... data) {
        if (data.length == 0) return null;
        final List<Class<?>> classes = new ArrayList<>();
        for (PostCompileClass datum : data) {
            final Class<?> part = this.createLoader().loadClass(datum.name(), datum.code());
            classes.add(part);
        }
        return assembleScript(classes.toArray(new Class[0]));
    }
    
    /**
     * Assembles a script from defined classes.
     *
     * @param loaded the classes
     * @return the script
     */
    public Script assembleScript(final Class<?>... loaded) {
        final Script script = new Script(false, this, null, loaded);
        this.scripts.add(script);
        return script;
    }
    
    /**
     * Loads scripts from compiled source code.
     *
     * @param data the class code
     * @return the scripts
     */
    public Collection<Script> loadScripts(final PostCompileClass[] data) {
        final List<Script> classes = new ArrayList<>();
        for (PostCompileClass datum : data) {
            classes.add(this.loadScript(datum));
        }
        return classes;
    }
    
    /**
     * Loads a script from a stream of its compiled bytecode.
     *
     * @param stream the compiled class code
     * @return the script
     * @throws IOException if something goes wrong
     */
    public Script loadScript(final InputStream stream)
        throws IOException {
        return loadScript(stream.readAllBytes());
    }
    
    /**
     * Loads a script from its compiled bytecode.
     * The name is determined by reading the class name entry from the code.
     *
     * @param bytecode the class code
     * @return the script
     * @throws IOException if the name cannot be read
     */
    public Script loadScript(final byte[] bytecode) throws IOException {
        return loadScript(bytecode, getClassName(new ByteArrayInputStream(bytecode)));
    }
    
    /**
     * Loads a script from its compiled bytecode and class name.
     *
     * @param bytecode the compiled class code
     * @param name     the class name
     * @return the script
     */
    public Script loadScript(final byte[] bytecode, final String name) {
        return loadScript(this.loadClass(name, bytecode));
    }
    
    /**
     * Loads a script from its compiled class file.
     *
     * @param source the compiled class
     * @return the script
     * @throws IOException if something goes wrong
     */
    public Script loadScript(final File source)
        throws IOException {
        try (final InputStream stream = new FileInputStream(source)) {
            return loadScript(stream, source.getName());
        }
    }
    
    /**
     * Loads a script from a stream of its compiled bytecode.
     *
     * @param stream the compiled class code
     * @param name   the class name
     * @return the script
     * @throws IOException if something goes wrong
     */
    public Script loadScript(final InputStream stream, final String name)
        throws IOException {
        return loadScript(this.loadClass(name, stream.readAllBytes()));
    }
    
    /**
     * Loads a script from its compiled class file.
     *
     * @param source the compiled class
     * @param name   the class name
     * @return the script
     * @throws IOException if something goes wrong
     */
    public Script loadScript(final File source, final String name)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return loadScript(this.loadClass(name, stream.readAllBytes()));
        }
    }
    
    /**
     * Generates a script thread from the given process.
     *
     * @param controller    the queue controller
     * @param runnable      the script instruction
     * @param inheritLocals whether to inherit thread variables
     * @return the new process
     */
    public Thread allocateProcess(final OperationController controller, final Runnable runnable, boolean inheritLocals) {
        return factory.newThread(controller, runnable, inheritLocals);
    }
    
    /**
     * The 'main' thread this runtime was given.
     * This is used mostly for locking.
     *
     * @return the main thread
     */
    public Thread getMainThread() {
        return mainThread;
    }
    
    /**
     * The scheduler service this runtime uses.
     *
     * @return the scheduler
     */
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
    
    /**
     * The executor service this runtime uses.
     * This is separate from the scheduler so that threads can be re-used more efficiently.
     *
     * @return the executor
     */
    public ExecutorService getExecutor() {
        return executor;
    }
    
    /**
     * Schedules a task to be run at some point in the future.
     *
     * @param runnable the task
     * @param millis   the execution delay
     * @return a future for the task
     */
    public Future<?> schedule(Runnable runnable, long millis) {
        return scheduler.schedule(runnable, millis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * This class handles the class-loading delegation for libraries and scripts.
     * It should not be interacted with directly, see {@link Skript#getLoader()} for an instance.
     */
    public static class RuntimeClassLoader extends ClassLoader implements ClassProvider {
        protected RuntimeClassLoader(ClassLoader parent) {
            super(parent);
        }
        
        @Override
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
        
        public Class<?> loadClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
        
        @Override
        public Class<?> loadClass(Class<?> aClass, String name, byte[] bytecode) {
            try {
                return Class.forName(name, false, this);
            } catch (ClassNotFoundException ex) {
                return super.defineClass(name, bytecode, 0, bytecode.length);
            }
        }
    }
    
    /**
     * This mirror handles class injection.
     * This should not be interacted with by an external program.
     */
    static class SkriptMirror extends Mirror<Object> {
        protected SkriptMirror(ClassProvider provider) {
            super(Skript.class);
            useProvider(provider);
        }
        
        @Override
        public Class<?> loadClass(String name, byte[] bytecode) {
            return super.loadClass(name, bytecode);
        }
    }
    
}
