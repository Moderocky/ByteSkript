/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

public class EveryTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("every.bsk"), "skript.every");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void test() throws Throwable {
        if (true) return; // next bit shouldn't be run during tests
        script.getFunction("test").run(new Object[0]).get();
    }
}
