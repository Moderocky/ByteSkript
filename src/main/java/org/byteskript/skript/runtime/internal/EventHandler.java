package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.threading.ScriptRunner;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    
    protected final List<ScriptRunner> triggers = new ArrayList<>();
    
    public void run(final Skript skript, final Event event) {
        for (ScriptRunner trigger : triggers) {
            skript.runScript(trigger, event);
        }
    }
    
    public void run(final Skript skript, final Event event, final Script script) {
        for (ScriptRunner trigger : triggers) {
            if (trigger.owner() == script.mainClass())
                skript.runScript(trigger, event);
        }
    }
    
    public void add(final ScriptRunner runner) {
        this.triggers.add(runner);
    }
    
}
