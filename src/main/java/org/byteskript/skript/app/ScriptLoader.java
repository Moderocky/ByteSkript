package org.byteskript.skript.app;

import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ScriptLoader extends SkriptApp {
    protected static final Skript SKRIPT = new Skript();
    
    static final List<Script> SCRIPTS = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        registerLibraries(SKRIPT);
        final Collection<Script> scripts = SKRIPT.compileLoadScripts(SOURCE);
        new SimpleThrottleController(SKRIPT).run();
        SCRIPTS.addAll(scripts);
    }
    
}
