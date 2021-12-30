/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ScriptLoader extends SkriptApp {
    private static final Skript SKRIPT = new Skript();
    
    static final List<Script> SCRIPTS = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        registerLibraries(SKRIPT);
        final Collection<Script> scripts = SKRIPT.compileLoadScripts(SOURCE);
        new SimpleThrottleController(SKRIPT).run();
        SCRIPTS.addAll(scripts);
    }
    
}
