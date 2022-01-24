/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import org.byteskript.skript.error.ScriptCompileError;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class Promise<Type> implements Future<Type> {
    
    protected final CompletableFuture<Type> future;
    protected volatile Type type;
    
    public Promise(CompletableFuture<Type> future) {
        this.future = future;
    }
    
    public boolean ready() {
        return future.isDone();
    }
    
    public void cancel() {
        this.future.cancel(true);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return future.isDone();
    }
    
    public synchronized Type get() {
        if (type == null) await();
        return type;
    }
    
    public synchronized void await() {
        try {
            this.type = future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new ScriptCompileError(-1, ex);
        }
    }
    
    @Override
    public synchronized Type get(long timeout, @NotNull TimeUnit unit) {
        try {
            return type = future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new ScriptCompileError(-1, ex);
        }
    }
    
    public void whenComplete(Consumer<Type> consumer) {
        this.future.thenAccept(consumer);
    }
    
}
