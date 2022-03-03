/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.threading.ScriptRunner;

import java.util.ArrayList;
import java.util.List;

@Ignore
public class EventHandler {
    
    protected final List<ScriptRunner> triggers = new ArrayList<>();
    
    public List<ScriptRunner> getTriggers() {
        return triggers;
    }
    
    public void run(final Skript skript, final Event event) {
        for (final ScriptRunner trigger : triggers) {
            skript.runScript(trigger, event);
        }
    }
    
    public void run(final Skript skript, final Event event, final Script script) {
        for (final ScriptRunner trigger : triggers) {
            if (trigger.owner() == script.mainClass())
                skript.runScript(trigger, event);
        }
    }
    
    public void add(final ScriptRunner runner) {
        this.triggers.add(runner);
    }
    
}
