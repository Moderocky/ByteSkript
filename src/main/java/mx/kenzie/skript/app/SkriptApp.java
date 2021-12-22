package mx.kenzie.skript.app;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.error.ScriptRuntimeError;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class SkriptApp {
    protected static final File SOURCE = new File("skript/");
    protected static final File RESOURCES = new File("resources/");
    protected static final File OUTPUT = new File("compiled/");
    
    protected static List<File> getFiles(List<File> files, Path root) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (final Path path : stream) {
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
    
    protected static PostCompileClass getData(final Class<?> type) throws IOException {
        return new PostCompileClass(getSource(type), type.getName(), new Type(type).internalName());
    }
    
    protected static byte[] getSource(final Class<?> cls) throws IOException {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream(cls.getName()
            .replace('.', '/') + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        }
    }
    
    protected static Class<?>[] findClasses(final String namespace) throws IOException, ClassNotFoundException {
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
