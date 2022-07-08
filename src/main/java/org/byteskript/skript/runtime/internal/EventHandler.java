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
import org.byteskript.skript.runtime.threading.ScriptFinishFuture;
import org.byteskript.skript.runtime.threading.ScriptRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Ignore
public class EventHandler {
    
    protected final List<ScriptRunner> triggers = new ArrayList<>();
    
    public List<ScriptRunner> getTriggers() {
        return triggers;
    }
    
    public ScriptFinishFuture[] run(final Skript skript, final Event event) {
        final ScriptFinishFuture[] futures = new ScriptFinishFuture[triggers.size()];
        int count = 0;
        synchronized (triggers) { // Reduce the chance of comodification.
            for (final ScriptRunner trigger : triggers) {
                futures[count] = (ScriptFinishFuture) skript.runScript(trigger, event);
                assert futures[count] != null: "Script trigger didn't produce a task.";
                count++;
            }
        }
        return futures;
    }
    
    public ScriptFinishFuture[] run(final Skript skript, final Event event, final Script script) {
        int count = 0;
        synchronized (triggers) {
            final List<ScriptRunner> triggers = new ArrayList<>(this.triggers);
            triggers.removeIf(trigger -> trigger.owner() != script.mainClass());
            final ScriptFinishFuture[] futures = new ScriptFinishFuture[triggers.size()];
            for (final ScriptRunner trigger : triggers) {
                futures[count] = (ScriptFinishFuture) skript.runScript(trigger, event);
                assert futures[count] != null: "Script trigger didn't produce a task.";
                count++;
            }
            return futures;
        }
    }
    
    public void add(final ScriptRunner runner) {
        synchronized (triggers) {
            this.triggers.add(runner);
        }
    }
    
}
