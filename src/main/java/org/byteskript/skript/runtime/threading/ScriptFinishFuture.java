/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.runtime.Skript;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class ScriptFinishFuture implements Supplier<Object>, Future<Object> {
    
    private final Skript skript;
    private final Object lock = new Object();
    private boolean done;
    public ScriptThread thread;
    protected Object value;
    
    public ScriptFinishFuture(Skript skript) {
        this.skript = skript;
    }
    
    public synchronized void value(Object object) {
        this.value = object;
    }
    
    public void finish() {
        synchronized (lock) {
            lock.notify();
        }
        synchronized (this) {
            this.done = true;
        }
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (thread != null && thread.isAlive()) {
            thread.stop();
            return true;
        } else return false;
    }
    
    @Override
    public boolean isCancelled() {
        return thread == null || thread.isAlive();
    }
    
    @Override
    public synchronized boolean isDone() {
        return value != null;
    }
    
    @Override
    public Object get() {
        synchronized (this) {
            if (this.done) return value;
        }
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        synchronized (this) {
            return value;
        }
    }
    
    @Override
    public Object get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this) {
            if (value != null) return value;
        }
        synchronized (lock) {
            lock.wait(unit.toMillis(timeout));
        }
        synchronized (this) {
            return value;
        }
    }
}
