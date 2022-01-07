/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.byteskript.skript.runtime.internal.ConsoleColour.*;

public class ByteSkriptApp extends SkriptApp {
    protected static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws Throwable {
        if (args.length < 1) {
            makeFiles();
            System.out.println(RESET + "Welcome to " + BRIGHT_PURPLE + "ByteSkript" + RESET + "!");
            System.out.println(RESET + "Available arguments:");
            System.out.println(RESET + "\trun        | " + CYAN + "Run scripts in the " + CYAN_UNDERLINED + "skripts/" + CYAN + " directory.");
            System.out.println(RESET + "\trun <file> | " + CYAN + "Run a single script in isolation.");
            System.out.println(RESET + "\tcompile    | " + CYAN + "Compile library class files for all scripts.");
            System.out.println(RESET + "\t           | " + CYAN + "Syntax-providing classes can be moved to the " + CYAN_UNDERLINED + "libraries/" + CYAN + " folder.");
            System.out.println(RESET + "\tjar <name> | " + CYAN + "Build a Jar file in " + CYAN_UNDERLINED + "compiled/" + CYAN + " from all your scripts.");
            System.out.println(RESET + "\t           | " + CYAN + "This will include files in " + CYAN_UNDERLINED + "resources/" + CYAN);
            System.out.println(RESET + "\tclean      | " + CYAN + "Cleans the " + CYAN_UNDERLINED + "compiled/" + CYAN + " folder.");
            System.out.print(RESET);
            System.out.println("Visit https://docs.byteskript.org for help and tutorials.");
        } else if (args[0].equalsIgnoreCase("clean")) {
            final List<File> files = getFiles(new ArrayList<>(), OUTPUT.toPath());
            int i = 50;
            while (!files.isEmpty() && i > 0) {
                files.removeIf(File::delete);
                i--;
            }
            if (!OUTPUT.exists()) OUTPUT.mkdirs();
        } else if (args[0].equalsIgnoreCase("run") && args.length < 2) {
            ScriptLoader.main();
        } else if (args[0].equalsIgnoreCase("run")) {
            final String name = args[1];
            final File file = new File(name);
            registerLibraries(SKRIPT);
            try (final InputStream stream = new FileInputStream(file)) {
                final PostCompileClass[] classes = SKRIPT.compileComplexScript(stream, "skript." + file.getName());
                for (final PostCompileClass type : classes) {
                    SKRIPT.loadScript(type);
                }
            }
            new SimpleThrottleController(SKRIPT).run();
        } else if (args[0].equalsIgnoreCase("jar")) {
            final String[] arguments;
            if (args.length > 1) {
                arguments = new String[args.length - 1];
                System.arraycopy(args, 1, arguments, 0, arguments.length);
            } else arguments = new String[0];
            ScriptJarBuilder.main(arguments);
        } else if (args[0].equalsIgnoreCase("compile")) {
            ScriptCompiler.main();
        }
    }
    
    static void makeFiles() {
        if (!SOURCE.exists()) SOURCE.mkdirs();
        if (!OUTPUT.exists()) OUTPUT.mkdirs();
        if (!LIBRARIES.exists()) LIBRARIES.mkdirs();
        if (!RESOURCES.exists()) RESOURCES.mkdirs();
    }
    
}
