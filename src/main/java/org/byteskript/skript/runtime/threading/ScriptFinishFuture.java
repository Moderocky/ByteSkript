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

public class ScriptFinishFuture implements Future<Object> {
    
    private final Skript skript;
    private final Object lock = new Object();
    public ScriptThread thread;
    protected Object value;
    
    public ScriptFinishFuture(Skript skript) {
        this.skript = skript;
    }
    
    public void value(Object object) {
        synchronized (this) {
            this.value = object;
        }
    }
    
    public void finish() {
        synchronized (lock) {
            lock.notify();
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
        return thread != null && !thread.isAlive();
    }
    
    @Override
    public boolean isDone() {
        return thread != null && !thread.isAlive();
    }
    
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            lock.wait();
        }
        synchronized (this) {
            return value;
        }
    }
    
    @Override
    public Object get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (lock) {
            lock.wait(unit.toMillis(timeout));
        }
        synchronized (this) {
            return value;
        }
    }
}
