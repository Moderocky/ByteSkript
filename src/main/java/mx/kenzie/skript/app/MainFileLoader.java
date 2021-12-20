package mx.kenzie.skript.app;

import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MainFileLoader {
    
    static final Skript SKRIPT = new Skript();
    static final String EXTENSION = "." + SkriptLangSpec.LANG.sourceFileExt();
    static final List<Script> SCRIPTS = new ArrayList<>();
    static final File FOLDER = new File("skript/");
    
    public static void main(String[] args) throws IOException {
        if (!FOLDER.exists()) FOLDER.mkdirs();
        final Collection<Script> scripts = SKRIPT.compileLoadScripts(FOLDER);
        new SimpleThrottleController(SKRIPT).run();
        SCRIPTS.addAll(scripts);
    }
    
}
