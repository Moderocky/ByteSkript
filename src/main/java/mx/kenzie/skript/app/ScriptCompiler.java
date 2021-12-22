package mx.kenzie.skript.app;

import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ScriptCompiler extends SkriptApp {
    protected static final Skript SKRIPT = new Skript();
    
    static final List<Script> SCRIPTS = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        if (!SOURCE.exists()) SOURCE.mkdirs();
        if (!OUTPUT.exists()) OUTPUT.mkdirs();
        SKRIPT.compileScripts(SOURCE, OUTPUT);
    }
    
}
