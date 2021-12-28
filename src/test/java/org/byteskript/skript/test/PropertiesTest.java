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

public class PropertiesTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass[] classes = skript.compileComplexScript(TypesTest.class.getClassLoader()
            .getResourceAsStream("properties.bsk"), "skript.properties");
        for (PostCompileClass cls : classes) {
            debug(cls);
            if (script == null)
                script = skript.loadScript(cls);
            else skript.loadScript(cls);
        }
    }
    
    @Test
    public void basic_use() throws Throwable {
        final Member function = script.getFunction("basic_use");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void simple_example() throws Throwable {
        final Member function = script.getFunction("simple_example");
        assert function != null;
        function.run(skript).get();
        function.invoke();
    }
    
}
