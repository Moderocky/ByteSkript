/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import mx.kenzie.jupiter.stream.Stream;
import org.byteskript.skript.compiler.DebugSkriptCompiler;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScriptDebugger extends SkriptApp {
    
    public static void main(String... args) throws IOException {
        final File file = new File(ROOT, "debug.txt");
        try (final FileOutputStream stream = new FileOutputStream(file)) {
            final Skript skript = new Skript(new DebugSkriptCompiler(Stream.controller(stream)));
            registerLibraries(skript);
            skript.compileScripts(SOURCE);
        }
    }
    
    public static void debug(File file) throws IOException {
        final File debug = new File(ROOT, "debug.txt");
        try (final FileOutputStream stream = new FileOutputStream(debug)) {
            try (final FileInputStream input = new FileInputStream(file)) {
                final Skript skript = new Skript(new DebugSkriptCompiler(Stream.controller(stream)));
                registerLibraries(skript);
                skript.compileScript(input, "skript/" + file.getName());
            }
        }
    }
}
