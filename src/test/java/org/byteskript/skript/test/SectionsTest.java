/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

public class SectionsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SectionsTest.class.getClassLoader()
            .getResourceAsStream("sections.bsk"), "skript.sections");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void relay_test() throws Throwable {
        script.getFunction("relay_test").invoke();
    }
    
    public static Object blob(String thing) {
        return "hello " + thing;
    }
    
}
