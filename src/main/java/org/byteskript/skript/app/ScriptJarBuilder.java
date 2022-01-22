/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import mx.kenzie.foundation.assembler.JarBuilder;
import mx.kenzie.foundation.assembler.Manifest;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.byteskript.skript.runtime.internal.ConsoleColour.*;
import static org.byteskript.skript.runtime.internal.ConsoleColour.RESET;

public final class ScriptJarBuilder extends SkriptApp {
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws IOException {
        registerLibraries(SKRIPT);
        final String name = args.length > 0 ? args[0] : "CompiledScripts";
        final PostCompileClass[] scripts = SKRIPT.compileScripts(SOURCE);
        final File jar = new File(OUTPUT, name + ".jar");
        compileResource(jar, scripts);
        System.out.println(RESET + "Available scripts have been compiled to " + CYAN + CYAN_UNDERLINED + "skripts/" + jar.getName() + RESET);
    }
    
    static void compileResource(File jar, PostCompileClass... classes) throws IOException {
        if (!jar.exists()) jar.createNewFile();
        final List<File> resources = getFiles(new ArrayList<>(), RESOURCES.toPath());
        final List<PostCompileClass> runtime = new ArrayList<>();
        scrapeRuntimeResources(runtime);
        try (final JarBuilder builder = new JarBuilder(jar)) {
            builder.write(runtime.toArray(new PostCompileClass[0]));
            builder.write(classes);
            for (final File resource : resources)
                try (final FileInputStream stream = new FileInputStream(resource)) {
                    builder.write(resource.getName(), stream);
                }
            final String version = ScriptJarBuilder.class.getPackage().getImplementationVersion();
            builder.manifest(new Manifest(ScriptRunner.class.getName(), "Skript Compiler " + version, "Skript Jar Builder"));
        }
    }
    
    static void scrapeRuntimeResources(final List<PostCompileClass> runtime) {
        for (final Library library : SKRIPT.getLoadedLibraries()) {
            runtime.addAll(library.getRuntime());
        }
    }
    
}
