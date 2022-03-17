/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;

public class GenericTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(GenericTest.class.getClassLoader()
            .getResourceAsStream("generic.bsk"), "skript.test");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void all() throws Throwable {
        final URI uri = GenericTest.class.getClassLoader().getResource("tests").toURI();
        final Path path;
        if (uri.getScheme().equals("jar")) {
            final FileSystem system = FileSystems.newFileSystem(uri, Collections.emptyMap());
            path = system.getPath("tests");
        } else {
            path = Paths.get(uri);
        }
        final Iterator<Path> iterator = Files.walk(path, 1).iterator();
        int failure = 0;
        while (iterator.hasNext()) {
            final Path file = iterator.next();
            if (!file.toString().endsWith(".bsk")) continue;
            final String part = file.toString().substring(file.toString().indexOf("/tests/") + 7);
            final String name = part.substring(0, part.length() - 4).replace(File.separatorChar, '.');
            try (final InputStream stream = Files.newInputStream(file)) {
                final PostCompileClass cls;
                synchronized (this) {
                    try {
                        cls = skript.compileScript(stream, "skript." + name);
                    } catch (Throwable ex) {
                        System.err.println("Error in '" + name + "':");
                        ex.printStackTrace(System.err);
                        failure++;
                        continue;
                    }
                    try {
                        final Script script = skript.loadScript(cls);
                        final boolean result = (boolean) script.getFunction("test").run(skript).get();
                        assert result : "Test failed.";
                    } catch (Throwable ex) {
                        System.err.println("Error in '" + name + "':");
                        ex.printStackTrace(System.err);
                        failure++;
                    }
                }
                final File test = new File("target/test-scripts/" + cls.name() + ".class");
                test.getParentFile().mkdirs();
                if (!test.exists()) test.createNewFile();
                try (final OutputStream output = new FileOutputStream(test)) {
                    output.write(cls.code());
                }
            }
        }
        assert failure < 1 : failure + " tests have failed.";
    }
    
    @Test
    public void generic_expressions() throws Throwable {
        final Member function = script.getFunction("generic_expressions");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void test_system() throws Throwable {
        final Member function = script.getFunction("test_system");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void test_method() throws Throwable {
        final Member function = script.getFunction("test_method");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void my_function() throws Throwable {
        final Member function = script.getFunction("my_function");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void testing_variable() throws Throwable {
        final Member function = script.getFunction("testing_variable");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void testing_list() throws Throwable {
        final Member function = script.getFunction("testing_list");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void testing_map() throws Throwable {
        final Member function = script.getFunction("testing_map");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void run_function() throws Throwable {
        final Member function = script.getFunction("run_function");
        assert function != null;
        assert "bees".equals(function.invoke());
    }
    
}
