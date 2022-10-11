/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Skript;
import org.junit.Test;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

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
