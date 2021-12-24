/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import org.byteskript.skript.error.ScriptLibraryError;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public abstract class SkriptApp {
    protected static final File ROOT;
    
    static {
        try {
            ROOT = new File(SkriptApp.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to get root file.");
        }
    }
    
    protected static final File SOURCE = new File(ROOT, "skript/");
    protected static final File RESOURCES = new File(ROOT, "resources/");
    protected static final File LIBRARIES = new File(ROOT, "libraries/");
    protected static final File OUTPUT = new File(ROOT, "compiled/");
    
    static {
        if (!SOURCE.exists()) SOURCE.mkdirs();
        if (!OUTPUT.exists()) OUTPUT.mkdirs();
        if (!LIBRARIES.exists()) LIBRARIES.mkdirs();
        if (!RESOURCES.exists()) RESOURCES.mkdirs();
    }
    
    protected static void registerLibraries(final Skript skript) {
        final List<File> files = getFiles(new ArrayList<>(), LIBRARIES.toPath());
        for (final File file : files) {
            if (file.getName().endsWith(".jar")) {
                try {
                    final JarFile jar = new JarFile(file);
                    final String main = jar.getManifest().getMainAttributes().getValue("Main-Class");
                    if (main == null)
                        throw new ScriptLibraryError("Library '" + file.getName() + "' is missing main class in manifest.");
                    callLibrary(file, main, skript);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            } else if (file.getName().endsWith(".class")) {
                // todo
            }
        }
    }
    
    protected static void callLibrary(final File file, final String main, final Skript skript) throws MalformedURLException, ClassNotFoundException {
        final URLClassLoader child = new URLClassLoader(
            new URL[]{file.toURI().toURL()},
            SkriptApp.class.getClassLoader()
        );
        final Class<?> target = Class.forName(main, true, child);
        try {
            target.getMethod("load", Skript.class)
                .invoke(null, skript);
        } catch (Throwable ex) {
            throw new ScriptLibraryError("Library '" + file.getName() + "' main class is missing load method:\n" +
                "static void load(Skript skript)");
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
