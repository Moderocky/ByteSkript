package mx.kenzie.skript.app;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.api.Library;
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
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws IOException {
        registerLibraries(SKRIPT);
        final String name = args.length > 0 ? args[0] : "CompiledScripts";
        final PostCompileClass[] scripts = SKRIPT.compileScripts(SOURCE);
        final File jar = new File(OUTPUT, name + ".jar");
        compileResource(jar, scripts);
    }
    
    static void scrapeRuntimeResources(final List<PostCompileClass> runtime) {
        for (final Library library : SKRIPT.getLoadedLibraries()) {
            runtime.addAll(library.getRuntime());
        }
    }
    
    static void compileResource(File jar, PostCompileClass... classes) throws IOException {
        final List<File> resources = getFiles(new ArrayList<>(), RESOURCES.toPath());
        final List<PostCompileClass> runtime = new ArrayList<>();
        scrapeRuntimeResources(runtime);
        try (
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(jar))) {
            writeResources(out, runtime.toArray(new PostCompileClass[0]));
            writeResources(out, classes);
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
    
    private static void writeResources(ZipOutputStream out, PostCompileClass[] classes) throws IOException {
        for (final PostCompileClass result : classes) {
            final ZipEntry entry = new ZipEntry(result.internalName() + ".class");
            out.putNextEntry(entry);
            final byte[] data = result.code();
            out.write(data, 0, data.length);
            out.closeEntry();
        }
    }
    
}
