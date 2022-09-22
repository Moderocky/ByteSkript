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
    
    private static final Skript skript
        = new Skript();
//    = new Skript(new DebugSkriptCompiler(Stream.controller(System.out)));
    
    public static void main(String[] args) throws Throwable { // test only
        System.setProperty("debug_mode", "true");
        final PostCompileClass cls = skript.compileScript(MainTest.class.getClassLoader()
            .getResourceAsStream("tests/bracket.bsk"), "skript.bracket");
    }
    
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
        final Skript.Test test = skript.new Test(true);
        test.testDirectory(path);
        final int failure = test.getFailureCount();
        for (final Throwable error : test.getErrors())
            synchronized (this) {
                error.printStackTrace(System.err);
            }
        assert failure < 1 : failure + " tests have failed.";
    }
    
}
