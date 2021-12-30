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

import static org.byteskript.skript.app.ByteSkriptApp.Colour.*;

public class ByteSkriptApp extends SkriptApp {
    protected static final Skript SKRIPT = new Skript();
    
    public static void main(String... args) throws Throwable {
        if (args.length < 1) {
            makeFiles();
            System.out.println(RESET + "Welcome to " + MAGENTA_BRIGHT + "ByteSkript" + RESET + "!");
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
    
    enum Colour {
        RESET("\033[0m"),
        BLACK("\033[0;30m"),
        RED("\033[0;31m"),
        GREEN("\033[0;32m"),
        YELLOW("\033[0;33m"),
        BLUE("\033[0;34m"),
        PURPLE("\033[0;35m"),
        CYAN("\033[0;36m"),
        WHITE("\033[0;37m"),
        
        // Bold
        BLACK_BOLD("\033[1;30m"),   // BLACK
        RED_BOLD("\033[1;31m"),     // RED
        GREEN_BOLD("\033[1;32m"),   // GREEN
        YELLOW_BOLD("\033[1;33m"),  // YELLOW
        BLUE_BOLD("\033[1;34m"),    // BLUE
        MAGENTA_BOLD("\033[1;35m"), // MAGENTA
        CYAN_BOLD("\033[1;36m"),    // CYAN
        WHITE_BOLD("\033[1;37m"),   // WHITE
        
        // Underline
        BLACK_UNDERLINED("\033[4;30m"),     // BLACK
        RED_UNDERLINED("\033[4;31m"),       // RED
        GREEN_UNDERLINED("\033[4;32m"),     // GREEN
        YELLOW_UNDERLINED("\033[4;33m"),    // YELLOW
        BLUE_UNDERLINED("\033[4;34m"),      // BLUE
        MAGENTA_UNDERLINED("\033[4;35m"),   // MAGENTA
        CYAN_UNDERLINED("\033[4;36m"),      // CYAN
        WHITE_UNDERLINED("\033[4;37m"),     // WHITE
        
        // Background
        BLACK_BACKGROUND("\033[40m"),   // BLACK
        RED_BACKGROUND("\033[41m"),     // RED
        GREEN_BACKGROUND("\033[42m"),   // GREEN
        YELLOW_BACKGROUND("\033[43m"),  // YELLOW
        BLUE_BACKGROUND("\033[44m"),    // BLUE
        MAGENTA_BACKGROUND("\033[45m"), // MAGENTA
        CYAN_BACKGROUND("\033[46m"),    // CYAN
        WHITE_BACKGROUND("\033[47m"),   // WHITE
        
        // High Intensity
        BLACK_BRIGHT("\033[0;90m"),     // BLACK
        RED_BRIGHT("\033[0;91m"),       // RED
        GREEN_BRIGHT("\033[0;92m"),     // GREEN
        YELLOW_BRIGHT("\033[0;93m"),    // YELLOW
        BLUE_BRIGHT("\033[0;94m"),      // BLUE
        MAGENTA_BRIGHT("\033[0;95m"),   // MAGENTA
        CYAN_BRIGHT("\033[0;96m"),      // CYAN
        WHITE_BRIGHT("\033[0;97m"),     // WHITE
        
        // Bold High Intensity
        BLACK_BOLD_BRIGHT("\033[1;90m"),    // BLACK
        RED_BOLD_BRIGHT("\033[1;91m"),      // RED
        GREEN_BOLD_BRIGHT("\033[1;92m"),    // GREEN
        YELLOW_BOLD_BRIGHT("\033[1;93m"),   // YELLOW
        BLUE_BOLD_BRIGHT("\033[1;94m"),     // BLUE
        MAGENTA_BOLD_BRIGHT("\033[1;95m"),  // MAGENTA
        CYAN_BOLD_BRIGHT("\033[1;96m"),     // CYAN
        WHITE_BOLD_BRIGHT("\033[1;97m"),    // WHITE
        
        // High Intensity backgrounds
        BLACK_BACKGROUND_BRIGHT("\033[0;100m"),     // BLACK
        RED_BACKGROUND_BRIGHT("\033[0;101m"),       // RED
        GREEN_BACKGROUND_BRIGHT("\033[0;102m"),     // GREEN
        YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),    // YELLOW
        BLUE_BACKGROUND_BRIGHT("\033[0;104m"),      // BLUE
        MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),   // MAGENTA
        CYAN_BACKGROUND_BRIGHT("\033[0;106m"),      // CYAN
        WHITE_BACKGROUND_BRIGHT("\033[0;107m");     // WHITE
        
        private final String code;
        
        Colour(String code) {
            this.code = code;
        }
        
        @Override
        public String toString() {
            if (System.console() == null || System.getenv().get("TERM") != null) {
                return code;
            } else {
                return "";
            }
        }
    }
    
}
