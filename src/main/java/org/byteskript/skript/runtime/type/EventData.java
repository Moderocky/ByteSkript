/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.runtime.threading.ScriptFinishFuture;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class EventData<Type extends Event> {
    private final boolean run;
    private final Type event;
    private final ScriptFinishFuture[] tasks;
    private CompletableFuture<?> all, any;
    private CompletableFuture<?>[] futures;
    
    public EventData(boolean run, Type event, ScriptFinishFuture[] tasks) {
        this.run = run;
        this.event = event;
        this.tasks = tasks;
    }
    
    private void prepareFutures() {
        this.futures = new CompletableFuture[tasks.length];
        for (int i = 0; i < tasks.length; i++) futures[i] = CompletableFuture.supplyAsync(tasks[i]);
    }
    
    public CompletableFuture<?> all() {
        if (all != null) return all;
        if (this.futures == null) this.prepareFutures();
        return all = CompletableFuture.allOf(futures);
    }
    
    public CompletableFuture<?> any() {
        if (any != null) return any;
        if (this.futures == null) this.prepareFutures();
        return any = CompletableFuture.anyOf(futures);
    }
    
    @SuppressWarnings("unchecked")
    public Class<Type> getType() {
        return (Class<Type>) event.getClass();
    }
    
    public boolean run() {
        return run;
    }
    
    public Type event() {
        return event;
    }
    
    public ScriptFinishFuture[] tasks() {
        return tasks;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EventData) obj;
        return this.run == that.run &&
            Objects.equals(this.event, that.event) &&
            Objects.equals(this.tasks, that.tasks);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(run, event, tasks);
    }
    
    @Override
    public String toString() {
        return "EventData[" +
            "run=" + run + ", " +
            "event=" + event + ", " +
            "tasks=" + tasks + ']';
    }
    
    
}
