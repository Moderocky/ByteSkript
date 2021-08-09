package mx.kenzie.skript.runtime.internal;

import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.threading.ScriptRunner;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    
    protected final List<ScriptRunner> triggers = new ArrayList<>();
    
    public void run(final Skript skript, final Event event) {
        for (ScriptRunner trigger : triggers) {
            skript.runScript(trigger, event);
        }
    }
    
    public void add(final ScriptRunner runner) {
        this.triggers.add(runner);
    }
    
}
