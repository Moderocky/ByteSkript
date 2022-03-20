/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsyncCompileTest {
    
    private static final Skript skript = new Skript();
    
    private static String code;
    
    @BeforeClass
    public static void warm() throws Throwable {
        code = new String(SyntaxTest.class.getClassLoader()
            .getResourceAsStream("tests/typemember.bsk").readAllBytes());
        final PostCompileClass cls = skript.compileScript(code, "skript.test");
        final PostCompileClass second = skript.compileScriptAsync(code, "skript.test").get()[0];
    }
    
    @Test
    public void test() throws Exception {
        final PostCompileClass[] classes = skript.compileComplexScript(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), "skript.speed");
        assert classes.length > 0;
        if (true) return; // testing
        final long start, end;
        start = System.nanoTime();
        for (int i = 0; i < 300; i++) {
            final PostCompileClass cls = skript.compileScript(code, "skript.speed");
        }
        end = System.nanoTime();
        System.out.println("Took " + (end - start) / 300 + " nanos each.");
    }
    
    @Test
    public void testAsync() throws Exception {
        final PostCompileClass[] classes = skript.compileScriptAsync(code, "skript.speed").get();
        assert classes.length > 0;
        if (true) return; // testing
        final long start, end;
        final List<Promise<PostCompileClass[]>> promises = new ArrayList<>();
        start = System.nanoTime();
        for (int i = 0; i < 300; i++) {
            promises.add(skript.compileScriptAsync(code, "skript.speed"));
        }
        for (final Promise<PostCompileClass[]> promise : promises) promise.await();
        end = System.nanoTime();
        System.out.println("Took " + (end - start) / 300 + " nanos each.");
    }
    
}
