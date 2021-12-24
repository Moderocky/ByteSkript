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

public class OperatorsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(OperatorsTest.class.getClassLoader()
            .getResourceAsStream("maths.bsk"), "skript.maths");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void basic() throws Throwable {
        final Member function = script.getFunction("basic");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void looping() throws Throwable {
        final Member function = script.getFunction("looping");
        assert function != null;
        function.invoke();
    }
    
}
