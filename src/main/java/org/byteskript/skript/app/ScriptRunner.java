/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.app;

import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ScriptRunner {
    protected static final Skript SKRIPT = new Skript(null); // no compiler available
    
    static final List<Script> SCRIPTS = new ArrayList<>();
    
    public static void main(String... args) throws IOException, ClassNotFoundException {
        final CodeSource src = ScriptRunner.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            final URL jar = src.getLocation();
            try (final ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                while (true) {
                    final ZipEntry entry = zip.getNextEntry();
                    if (entry == null) break;
                    if (entry.isDirectory()) continue;
                    final String name = entry.getName();
                    if (!name.endsWith(".class")) continue;
                    if (name.startsWith("skript/")) {
                        SCRIPTS.add(SKRIPT.loadScript(zip.readAllBytes()));
                    }
                }
            }
        } else {
            throw new ScriptRuntimeError("Unable to access source.");
        }
        new SimpleThrottleController(SKRIPT).run();
    }
}
