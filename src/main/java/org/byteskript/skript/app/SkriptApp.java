/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import org.byteskript.skript.error.ScriptLibraryError;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.LibraryClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public abstract class SkriptApp {
    protected static final File ROOT = getRoot();
    protected static final File SOURCE = new File(ROOT, "skript/");
    protected static final File RESOURCES = new File(ROOT, "resources/");
    protected static final File LIBRARIES = new File(ROOT, "libraries/");
    protected static final File OUTPUT = new File(ROOT, "compiled/");
    
    private static File getRoot() {
        try {
            return new File(SkriptApp.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to get root file.", e);
        }
    }
    
    protected static File getJar() {
        try {
            return new File(SkriptApp.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to find source file.", e);
        }
    }
    
    protected static void registerLibraries(final Skript skript) {
        final List<File> files = getFiles(new ArrayList<>(), LIBRARIES.toPath());
        for (final File file : files) {
            if (file.getName().endsWith(".jar")) {
                try (final JarFile jar = new JarFile(file)) {
                    final String main = jar.getManifest().getMainAttributes().getValue("Main-Class");
                    if (main == null)
                        throw new ScriptLibraryError("Library '" + file.getName() + "' is missing a main class in its Jar manifest.");
                    callLibrary(file, main, skript);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            } else if (file.getName().endsWith(".class")) {
                try (final InputStream stream = new FileInputStream(file)) {
                    skript.registerLibraryClass(stream.readAllBytes());
                } catch (Throwable exception) {
                    throw new ScriptLibraryError("Error while loading class library '" + file.getName() + "'", exception);
                }
            }
        }
    }
    
    protected static void callLibrary(final File file, final String main, final Skript skript) throws IOException, ClassNotFoundException {
        final LibraryClassLoader child = new LibraryClassLoader(file, SkriptApp.class.getClassLoader());
        final Class<?> target = Class.forName(main, true, child);
        try {
            target.getMethod("load", Skript.class).invoke(null, skript);
        } catch (NoSuchMethodException ex) {
            throw new ScriptLibraryError("Library '" + file.getName() + "' main class is missing a load method:\n" +
                "public static void load(Skript skript)\n");
        } catch (IllegalAccessException ex) {
            throw new ScriptLibraryError("Library '" + file.getName() + "' main class has no public load method:\n" +
                "public static void load(Skript skript)\n");
        } catch (Throwable ex) {
            throw new ScriptLibraryError("Error encountered while loading '" + file.getName() + "':", ex);
        }
    }
    
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
    
}
