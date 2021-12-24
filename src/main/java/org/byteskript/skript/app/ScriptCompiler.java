package org.byteskript.skript.app;

import org.byteskript.skript.runtime.Skript;

import java.io.IOException;

public final class ScriptCompiler extends SkriptApp {
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String[] args) throws IOException {
        registerLibraries(SKRIPT);
        SKRIPT.compileScripts(SOURCE, OUTPUT);
    }
    
}
