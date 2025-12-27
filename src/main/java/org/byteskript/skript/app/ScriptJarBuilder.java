/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import mx.kenzie.foundation.assembler.JarBuilder;
import mx.kenzie.foundation.assembler.Manifest;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.resource.Resource;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.byteskript.skript.runtime.internal.ConsoleColour.*;

public final class ScriptJarBuilder extends SkriptApp {
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws IOException {
        registerLibraries(SKRIPT);
        final String name = args.length > 0 ? args[0] : "CompiledScripts";
        final Resource[] scripts = SKRIPT.compileScripts(SOURCE);
        final File jar = new File(OUTPUT, name + ".jar");
        compileResource(jar, scripts);
        System.out.println(RESET + "Available scripts have been compiled to " + CYAN + CYAN_UNDERLINED + "skripts/" + jar.getName() + RESET);
    }
    
    static void compileResource(File jar, Resource... compiledResources) throws IOException {
        if (!jar.exists()) jar.createNewFile();
        final List<Resource> runtime = new ArrayList<>(Arrays.asList(compiledResources));
        scrapeRuntimeResources(runtime);
        for (final File file : getFiles(new ArrayList<>(), RESOURCES.toPath())) {
            runtime.add(Resource.ofFile(file.getName(), file));
        }
        try (final JarBuilder builder = new JarBuilder(jar)) {
            for (final Resource resource : runtime) {
                builder.write(resource.getEntryName(), resource.open());
            }
            final String version = ScriptJarBuilder.class.getPackage().getImplementationVersion();
            builder.manifest(new Manifest(ScriptRunner.class.getName(), "Skript Compiler " + version, "Skript Jar Builder"));
        }
    }
    
    static void scrapeRuntimeResources(final List<Resource> runtime) {
        for (final Library library : SKRIPT.getLoadedLibraries()) {
            runtime.addAll(library.getRuntime());
        }
    }
    
}
