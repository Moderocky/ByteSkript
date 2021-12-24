/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import org.byteskript.skript.runtime.Skript;

import java.io.IOException;

public final class ScriptCompiler extends SkriptApp {
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String[] args) throws IOException {
        registerLibraries(SKRIPT);
        SKRIPT.compileScripts(SOURCE, OUTPUT);
    }
    
}
