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

public class TypesTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass[] classes = skript.compileComplexScript(TypesTest.class.getClassLoader()
            .getResourceAsStream("types.bsk"), "skript.types");
        for (PostCompileClass cls : classes) {
            if (script == null)
                script = skript.loadScript(cls);
            else skript.loadScript(cls);
        }
    }
    
    @Test
    public void runnable() throws Throwable {
        final Member function = script.getFunction("runnable");
        assert function != null;
        function.run(skript).get();
    }
    
    @Test
    public void basic_use() throws Throwable {
        final Member function = script.getFunction("basic_use");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void multiple_inheritance() throws Throwable {
        final Member function = script.getFunction("multiple_inheritance");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void java_implement() throws Throwable {
        final Member function = script.getFunction("java_implement");
        assert function != null;
        function.invoke();
    }
    
}
