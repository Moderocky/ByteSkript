package mx.kenzie.skript.runtime;

import mx.kenzie.foundation.RuntimeClassLoader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.SimpleSkriptCompiler;
import mx.kenzie.skript.compiler.SkriptCompiler;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.error.ScriptLoadError;
import mx.kenzie.skript.runtime.threading.OperationController;
import mx.kenzie.skript.runtime.threading.SkriptThreadProvider;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Skript {
    
    public static final ThreadGroup THREAD_GROUP = new ThreadGroup("skript");
    final ScheduledExecutorService scheduler;
    final SkriptThreadProvider factory;
    final Thread mainThread;
    final SkriptCompiler compiler;
    final List<OperationController> processes;
    RuntimeClassLoader loader;
    
    public Skript() {
        this(new SkriptThreadProvider(), new SimpleSkriptCompiler(), Thread.currentThread(), new RuntimeClassLoader(Skript.class.getClassLoader()));
    }
    
    public Skript(SkriptThreadProvider threadProvider, SkriptCompiler compiler, Thread main, RuntimeClassLoader loader) {
        this.compiler = compiler;
        this.factory = threadProvider;
        this.mainThread = main;
        this.scheduler = new ScheduledThreadPoolExecutor(4, factory);
        this.loader = loader;
        this.processes = new ArrayList<>();
    }
    
    public Skript(Thread main, ClassLoader parent) {
        this(new SkriptThreadProvider(), new SimpleSkriptCompiler(), main, new RuntimeClassLoader(parent));
    }
    
    //region Script Control
    public Collection<OperationController> getProcesses() {
        return processes;
    }
    
    private OperationController createController() {
        return new OperationController(this, factory); // todo
    }
    
    @Deprecated
    public Thread runScript(final Method method, final Object... params) {
        final OperationController controller = createController();
        final Runnable runnable = () -> {
            try {
                method.invoke(null, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            controller.kill();
        };
        return factory.newThread(controller, runnable, true);
    }
    //endregion
    
    //region Control
    @Deprecated
    public void start(final RuntimeClassLoader loader) {
        if (loader != null) throw new IllegalStateException("Skript instance is already running.");
        this.loader = loader;
    }
    
    public boolean running() {
        return loader != null;
    }
    
    @SuppressWarnings("all")
    public void stop() {
        if (loader == null) throw new IllegalStateException("Skript instance is not running.");
        THREAD_GROUP.stop();
        loader = null;
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
    public Collection<File> compileScripts(final File root, final File outputDirectory)
        throws IOException {
        if (!root.exists()) throw new ScriptLoadError("Root folder does not exist.");
        if (!root.isDirectory()) throw new ScriptLoadError("Root must be a folder.");
        final List<File> files = getFiles(new ArrayList<>(), root.toPath());
        final List<File> outputs = new ArrayList<>();
        final int length = root.getAbsolutePath().length();
        for (File file : files) {
            try (InputStream input = new FileInputStream(file)) {
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
        for (File file : files) {
            try (InputStream a = new FileInputStream(file); InputStream b = new FileInputStream(file)) {
                final String name = getClassName(a);
                scripts.add(loadScript(b, name));
            }
        }
        return scripts;
    }
    
    public Script loadScript(final PostCompileClass datum) {
        return new Script(null, loader.loadClass(datum.name(), datum.code()));
    }
    
    public Collection<Script> loadScripts(final PostCompileClass[] data) {
        final List<Script> classes = new ArrayList<>();
        for (PostCompileClass datum : data) {
            classes.add(this.loadScript(datum));
        }
        return classes;
    }
    
    public Script loadScript(final byte[] bytecode, final String name) {
        return new Script(null, loader.loadClass(name, bytecode));
    }
    
    public Script loadScript(final byte[] bytecode) throws IOException {
        return loadScript(bytecode, getClassName(new ByteArrayInputStream(bytecode)));
    }
    
    public Script loadScript(final InputStream stream, final String name)
        throws IOException {
        return new Script(null, loader.loadClass(name, stream.readAllBytes()));
    }
    
    public Script loadScript(final InputStream stream)
        throws IOException {
        return loadScript(stream.readAllBytes());
    }
    
    public Script loadScript(final File source)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return loadScript(stream);
        }
    }
    
    public Script loadScript(final File source, final String name)
        throws IOException {
        try (InputStream stream = new FileInputStream(source)) {
            return new Script(source, loader.loadClass(name, stream.readAllBytes()));
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
        if (index == -1) return path;
        return path.substring(0, index - 1).replace(File.pathSeparator, ".");
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
