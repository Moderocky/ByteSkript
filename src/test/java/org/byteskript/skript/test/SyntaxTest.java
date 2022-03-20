/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.ConsoleColour;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SyntaxTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    
    @Test
    public void all() throws Throwable {
        final URI uri = SyntaxTest.class.getClassLoader().getResource("tests").toURI();
        final Path path;
        if (uri.getScheme().equals("jar")) {
            final FileSystem system = FileSystems.newFileSystem(uri, Collections.emptyMap());
            path = system.getPath("tests");
        } else {
            path = Paths.get(uri);
        }
        final Iterator<Path> iterator = Files.walk(path, 1).iterator();
        int failure = 0;
        final List<Throwable> errors = new ArrayList<>();
        synchronized (System.out) {
            while (iterator.hasNext()) {
                final Path file = iterator.next();
                if (!file.toString().endsWith(".bsk")) continue;
                final String part = file.toString().substring(file.toString().indexOf("/tests/") + 7);
                final String name = part.substring(0, part.length() - 4).replace(File.separatorChar, '.');
                System.out.println(ConsoleColour.RESET + "Running test '" + ConsoleColour.GREEN + name + ConsoleColour.RESET + "':");
                try (final InputStream stream = Files.newInputStream(file)) {
                    final PostCompileClass[] classes;
                    synchronized (this) {
                        try {
                            final long now, then;
                            now = System.currentTimeMillis();
                            classes = skript.compileComplexScript(stream, "skript." + name);
                            then = System.currentTimeMillis();
                            System.out.println(ConsoleColour.GREEN + "\t✓ " + ConsoleColour.RESET + "Parsed in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                        } catch (Throwable ex) {
                            System.out.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to parse.");
                            System.out.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to run.");
                            errors.add(ex);
                            failure++;
                            continue;
                        }
                        try {
                            final long now, then;
                            final Script script = skript.loadScripts(classes).iterator().next();
                            now = System.currentTimeMillis();
                            final boolean result;
                            synchronized (System.err) {
                                final Object object = script.getFunction("test").run(skript).get();
                                result = Boolean.TRUE.equals(object);
                            }
                            then = System.currentTimeMillis();
                            if (result)
                                System.out.println(ConsoleColour.GREEN + "\t✓ " + ConsoleColour.RESET + "Run in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                            else {
                                System.out.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Run in " + ConsoleColour.BLUE + (then - now) + ConsoleColour.RESET + " milliseconds.");
                                failure++;
                            }
                        } catch (Throwable ex) {
                            System.out.println(ConsoleColour.RED + "\t✗ " + ConsoleColour.RESET + "Failed to run.");
                            errors.add(ex);
                            failure++;
                        }
                    }
                    final File test = new File("target/test-scripts/" + classes[0].name() + ".class");
                    test.getParentFile().mkdirs();
                    if (!test.exists()) test.createNewFile();
                    try (final OutputStream output = new FileOutputStream(test)) {
                        output.write(classes[0].code());
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            for (final Throwable error : errors)
                synchronized (this) {
                    error.printStackTrace(System.err);
                }
        }
        assert failure < 1 : failure + " tests have failed.";
    }
    
}
