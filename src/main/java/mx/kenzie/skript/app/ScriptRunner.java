package mx.kenzie.skript.app;

import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptRunner extends SkriptApp {
    protected static final Skript SKRIPT = new Skript(null); // no compiler available
    
    static final List<Script> SCRIPTS = new ArrayList<>();
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final Class<?>[] classes = findClasses("skript/");
        for (Class<?> source : classes) {
            final Script script = SKRIPT.loadScript(source);
            SCRIPTS.add(script);
        }
        new SimpleThrottleController(SKRIPT).run();
    }
    
}
