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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Ignore
public class EventHandler {
    
    protected final List<ScriptRunner> triggers = new ArrayList<>();
    
    public List<ScriptRunner> getTriggers() {
        return triggers;
    }
    
    public CompletableFuture<Void> run(final Skript skript, final Event event) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (final ScriptRunner trigger : triggers) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    skript.runScript(trigger, event).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
    
    public CompletableFuture<Void> run(final Skript skript, final Event event, final Script script) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (final ScriptRunner trigger : triggers) {
            if (trigger.owner() == script.mainClass())
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        skript.runScript(trigger, event).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }));
        }
        
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
    
    public void add(final ScriptRunner runner) {
        this.triggers.add(runner);
    }
    
}
