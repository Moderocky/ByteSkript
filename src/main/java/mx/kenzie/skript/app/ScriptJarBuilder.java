package mx.kenzie.skript.app;

import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.compiler.SkriptCompiler;
import mx.kenzie.skript.runtime.Skript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ScriptJarBuilder extends SkriptApp {
    protected static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws IOException, ClassNotFoundException {
        final String name = args.length > 0 ? args[0] : "CompiledScripts";
        if (!SOURCE.exists()) SOURCE.mkdirs();
        if (!OUTPUT.exists()) OUTPUT.mkdirs();
        if (!RESOURCES.exists()) RESOURCES.mkdirs();
        final PostCompileClass[] scripts = SKRIPT.compileScripts(SOURCE);
        final File jar = new File(OUTPUT, name + ".jar");
        compileResource(jar, scripts);
    }
    
    static void compileResource(File jar, PostCompileClass... classes) throws IOException, ClassNotFoundException {
        final List<File> resources = getFiles(new ArrayList<>(), RESOURCES.toPath());
        final List<PostCompileClass> runtime = new ArrayList<>();
        runtime:
        {
            for (final Class<?> source : findClasses("mx/kenzie/skript/runtime/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("mx/kenzie/skript/error/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("mx/kenzie/skript/api/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("mx/kenzie/mimic/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("mx/kenzie/glass/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("mx/kenzie/mirror/")) {
                runtime.add(getData(source));
            }
            for (final Class<?> source : findClasses("org/objectweb/asm/")) {
                runtime.add(getData(source));
            }
            runtime.add(getData(Class.forName("skript")));
            runtime.add(getData(SkriptApp.class));
            runtime.add(getData(SkriptCompiler.class));
            runtime.add(getData(Compiler.class));
            runtime.add(getData(ScriptRunner.class));
            runtime.add(getData(SimpleThrottleController.class));
        }
        try (
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(jar))) {
            for (final PostCompileClass result : runtime) {
                final ZipEntry entry = new ZipEntry(result.internalName() + ".class");
                out.putNextEntry(entry);
                final byte[] data = result.code();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            for (final PostCompileClass result : classes) {
                final ZipEntry entry = new ZipEntry(result.internalName() + ".class");
                out.putNextEntry(entry);
                final byte[] data = result.code();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            for (final File resource : resources) {
                final ZipEntry entry = new ZipEntry(resource.getName());
                out.putNextEntry(entry);
                try (final FileInputStream stream = new FileInputStream(resource)) {
                    final byte[] data = stream.readAllBytes();
                    out.write(data, 0, data.length);
                }
                out.closeEntry();
            }
            manifest:
            {
                final ZipEntry entry = new ZipEntry("META-INF/MANIFEST.MF");
                out.putNextEntry(entry);
                final String version = ScriptJarBuilder.class.getPackage().getImplementationVersion();
                final byte[] data = ("Manifest-Version: 1.0\n" +
                    ("Main-Class: " + ScriptRunner.class.getName() + "\n") +
                    "Archiver-Version: Zip\n" +
                    "Created-By: Skript Compiler " + version + "\n" +
                    "Built-By: Skript Jar Builder\n").getBytes(StandardCharsets.UTF_8);
                out.write(data, 0, data.length);
                out.closeEntry();
            }
        }
    }
    
}
