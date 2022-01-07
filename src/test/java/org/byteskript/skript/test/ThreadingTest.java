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

public class ThreadingTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static final Skript skript2 = new Skript();
    private static Script script;
    private static Script script2;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(ProvidedFunctionsTest.class.getClassLoader()
            .getResourceAsStream("flow.bsk"), "skript.flow");
        script = skript.loadScript(cls);
        script2 = skript2.loadScript(cls);
    }
    
    @Test
    public void sleep_test() throws Throwable {
        final Member function = script.getFunction("sleep_flow");
        assert function != null;
        function.run(skript).get();
    }
    
    @Test
    public void multipleProcess() throws Throwable {
        final Member function = script.getFunction("sleep_flow");
        final Member function2 = script2.getFunction("sleep_flow");
        function.run(skript).get();
        function.run(skript2).get();
        function2.run(skript2).get();
        assert script.mainClass() != script2.mainClass();
        assert script.skriptInstance() != script2.skriptInstance();
    }
    
}
