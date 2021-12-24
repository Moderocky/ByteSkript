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