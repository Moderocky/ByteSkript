package mx.kenzie.skript.runtime;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.mirror.Mirror;
import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.SkriptCompiler;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.error.ScriptLoadError;
import mx.kenzie.skript.runtime.internal.EventHandler;
import mx.kenzie.skript.runtime.internal.GlobalVariableMap;
import mx.kenzie.skript.runtime.internal.Instruction;
import mx.kenzie.skript.runtime.internal.ModifiableCompiler;
import mx.kenzie.skript.runtime.threading.*;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public final class Skript {
    
    public static final ThreadGroup THREAD_GROUP = new ThreadGroup("skript");
    private static Skript skript;
    private static ExecutorService executor;
    static SkriptThreadProvider factory;
    final ScheduledExecutorService scheduler;
    final Thread mainThread;
    final ModifiableCompiler compiler;
    final List<OperationController> processes;
    final Map<Class<? extends Event>, EventHandler> events;
    final SkriptMirror mirror = new SkriptMirror(Skript.class);
    static final GlobalVariableMap VARIABLES = new GlobalVariableMap();
    
    static class SkriptMirror extends Mirror<Object> {
        protected SkriptMirror(Object target) {
            super(target);
        }
        
        @Override
        public Class<?> loadClass(String name, byte[] bytecode) {
            return super.loadClass(name, bytecode);
        }
    }
    
    public Skript(ModifiableCompiler compiler) {
        this(new SkriptThreadProvider(), compiler, Thread.currentThread());
    }
    
    public Skript() {
        this(new SkriptThreadProvider(), SkriptCompiler.createBasic(), Thread.currentThread());
    }
    
    public Skript(SkriptThreadProvider threadProvider, ModifiableCompiler compiler, Thread main) {
        this.compiler = compiler;
        factory = threadProvider;
        executor = Executors.newCachedThreadPool(factory);
        this.mainThread = main;
        this.scheduler = new ScheduledThreadPoolExecutor(4, factory);
        this.processes = new ArrayList<>();
        this.events = new HashMap<>();
        skript = this;
    }
    
    public static GlobalVariableMap getVariables() {
        return VARIABLES;
    }
    
    public static Skript currentInstance() {
        return skript;
    }
    
    //region Thread Control
    public static void runOnAsyncThread(final Instruction<?> runnable) {
        executor.submit(runnable::runSafely);
    }
    
    public static Future<?> getOnAsyncThread(final Instruction<?> runnable) {
        return executor.submit(runnable::get);
    }
    
    public static void runOnAsyncThread(final Runnable runnable) {
        executor.submit(runnable);
    }
    //endregion
    
    //region Script Control
    public Collection<OperationController> getProcesses() {
        return processes;
    }
    
    private OperationController createController() {
        return new OperationController(this, factory); // todo
    }
    
    public Future<?> runScript(final ScriptRunner runner) {
        return runScript(runner, null);
    }
    
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
//        return executor.submit(() -> {
//            final ScriptThread thread = (ScriptThread) Thread.currentThread();
//            thread.variables.clear();
//            thread.initiator = runner.owner();
//            thread.event = event;
//            try {
//                runner.run();
//            } catch (ThreadDeath ignore) {
//            } catch (Throwable ex) {
//                ex.printStackTrace(); // todo
//            }
//        });
    }
    
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
    
    public void registerEventHandler(final Class<? extends Event> event, final ScriptRunner runner) {
        this.events.putIfAbsent(event, new EventHandler());
        this.events.get(event).add(runner);
    }
    //endregion
    
    //region Control
    @Deprecated
    public void start() {
        throw new IllegalStateException("Skript instance is already running.");
    }
    
    public boolean running() {
        return mirror != null;
    }
    
    @SuppressWarnings("all")
    public void stop() {
        if (mirror == null) throw new IllegalStateException("Skript instance is not running.");
        THREAD_GROUP.stop();
    }
    //endregion
    
    //region Libraries
    public boolean registerLibrary(Library library) {
        return compiler.addLibrary(library);
    }
    
    public boolean unregisterLibrary(Library library) {
        return compiler.removeLibrary(library);
    }
    //endregion
    
    //region Script Compiling
    public boolean hasCompiler() {
        return compiler != null;
    }
    
    public ModifiableCompiler getCompiler() {
        return compiler;
    }
    
    public Library[] getLoadedLibraries() {
        if (!this.hasCompiler()) return new Library[0];
        return compiler.getLibraries();
    }
    
    public Collection<File> compileScripts(final File root, final File outputDirectory)
        throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<File> outputs = new ArrayList<>();
        final int length = root.getAbsolutePath().length();
        for (final File file : files) {
            try (final InputStream input = new FileInputStream(file)) {
                final String name = createClassName(file.getName(), file.getAbsolutePath().substring(length));
                outputs.addAll(compileComplexScript(input, name, outputDirectory));
            }
        }
        return outputs;
    }
    
    public void compileScript(final InputStream input, final String name, final OutputStream target)
        throws IOException {
        final PostCompileClass datum = compileScript(input, name);
        target.write(datum.code());
    }
    
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
                scripts.add(compileScript(stream, name));
            }
        }
        return scripts.toArray(new PostCompileClass[0]);
    }
    
    public PostCompileClass[] compileComplexScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        return classes;
    }
    
    public PostCompileClass compileScript(final InputStream stream, final String name) {
        final PostCompileClass[] classes = compiler.compile(stream, new Type(name));
        if (classes.length < 1) throw new ScriptCompileError(-1, "Script does not compile to a class.");
        if (classes.length > 1) throw new ScriptCompileError(-1, "Script compiles to multiple classes.");
        return classes[0];
    }
    //endregion
    
    //region Script Loading
    @Deprecated
    public Script compileLoad(InputStream stream, String name) {
        return loadScript(compileScript(stream, name));
    }
    
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
    
    public Script loadScript(final Class<?> loaded) {
        return new Script(this, null, loaded);
    }
    
    public Script loadScript(final PostCompileClass datum) {
        return new Script(this, null, mirror.loadClass(datum.name(), datum.code()));
    }
    
    public Collection<Script> loadScripts(final PostCompileClass[] data) {
        final List<Script> classes = new ArrayList<>();
        for (PostCompileClass datum : data) {
            classes.add(this.loadScript(datum));
        }
        return classes;
    }
    
    public Script loadScript(final byte[] bytecode, final String name) {
        return new Script(this, null, mirror.loadClass(name, bytecode));
    }
    
    public Script loadScript(final byte[] bytecode) throws IOException {
        return loadScript(bytecode, getClassName(new ByteArrayInputStream(bytecode)));
    }
    
    public Script loadScript(final InputStream stream, final String name)
        throws IOException {
        return new Script(this, null, mirror.loadClass(name, stream.readAllBytes()));
    }
    
    public Script loadScript(final InputStream stream)
        throws IOException {
        return loadScript(stream.readAllBytes());
    }
    
    public Script loadScript(final File source)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return loadScript(stream, source.getName()); // todo
        }
    }
    
    public Script loadScript(final File source, final String name)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return new Script(this, source, mirror.loadClass(name, stream.readAllBytes()));
        }
    }
    //endregion
    
    //region Timings
    public Thread allocateProcess(final OperationController controller, final Runnable runnable, boolean inheritLocals) {
        return factory.newThread(controller, runnable, inheritLocals);
    }
    
    public Thread getMainThread() {
        return mainThread;
    }
    
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
    
    public Future<?> schedule(Runnable runnable, long millis) {
        return scheduler.schedule(runnable, millis, TimeUnit.MILLISECONDS);
    }
    //endregion
    
    //region File Utilities
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
                    files.add(path.toAbsolutePath().toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
    //endregion
    
}
