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

public class ProvidedFunctionsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(ProvidedFunctionsTest.class.getClassLoader()
            .getResourceAsStream("provided_functions.bsk"), "skript.provided_functions");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void generic() throws Throwable {
        final Member function = script.getFunction("generic");
        assert function != null;
        function.invoke(skript);
    }
    
    @Test
    public void handles() throws Throwable {
        final Member function = script.getFunction("handles");
        assert function != null;
        function.invoke(skript);
    }
    
    @Test
    public void maths() throws Throwable {
        final Member function = script.getFunction("maths");
        assert function != null;
        function.invoke(skript);
    }
    
}
