/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.api.Event;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.CompiledScript;
import org.byteskript.skript.runtime.internal.ThreadVariableMap;

import java.io.PrintWriter;

/**
 * A script thread.
 * All scripts should run on script threads.
 * <p>
 * This class holds special metadata that needs to be accessed from any point on the process.
 * While unorthodox, this is significantly more efficient than keeping a collection of atomic maps.
 * <p>
 * This is also used to hold the class-loader and process from being garbage-collected prematurely.
 */
public class ScriptThread extends Thread {
    
    public final AirlockQueue queue;
    public final OperationController controller;
    public final Object lock = new Object();
    public final ThreadVariableMap variables = new ThreadVariableMap();
    public final Skript skript;
    public Class<? extends CompiledScript> initiator;
    public Event event;
    private PrintWriter writer;
    
    public ScriptThread(final Skript skript, final OperationController controller, ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
        this.skript = skript;
        this.controller = controller;
        this.queue = controller.queue;
    }
    
    public void println(Object object) {
        if (writer != null) writer.println(object);
        else skript.println(object);
    }
    
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}
