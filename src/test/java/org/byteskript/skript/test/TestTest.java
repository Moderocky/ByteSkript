/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SectionsTest.class.getClassLoader()
            .getResourceAsStream("test.bsk"), "skript.test");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void nonTest() throws Throwable {
        ExtractedSyntaxCalls.setTest(false);
        final Member function = script.getFunction("test_func");
        assert function != null;
        assert (int) function.invoke() == 10;
    }
    
    @Test
    public void inTest() throws Throwable {
        ExtractedSyntaxCalls.setTest(true);
        final Member function = script.getFunction("test_func");
        assert function != null;
        assert (int) function.invoke() == 5;
        ExtractedSyntaxCalls.setTest(false);
    }
    
}
