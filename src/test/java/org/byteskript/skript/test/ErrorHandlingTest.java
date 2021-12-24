package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class ErrorHandlingTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        skript.registerLibrary(new TestingLibrary());
        final PostCompileClass cls = skript.compileScript(ErrorHandlingTest.class.getClassLoader()
            .getResourceAsStream("errors.bsk"), "skript.errors");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void try_catch() throws Throwable {
        final Member function = script.getFunction("try_catch");
        assert function != null;
        function.run(skript);
    }
    
    public void errorMessage() throws Throwable {
        final PostCompileClass test = skript.compileScript(new ByteArrayInputStream("""
            function test_error:
                trigger:
                    set {var} to 1
                    run my_func()
                    exit program
                    
            function my_func:
                trigger:
                    run another_func()
                    
            function another_func:
                trigger:
                    set {myvar} to 2
                    throw exception
                
            """.getBytes(StandardCharsets.UTF_8)), "skript.error_test");
        final Member function = skript.loadScript(test).getFunction("test_error");
        assert function != null;
        function.run(skript);
    }
    
}
