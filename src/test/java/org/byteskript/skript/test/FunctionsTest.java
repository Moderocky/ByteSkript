package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class FunctionsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FunctionsTest.class.getClassLoader()
            .getResourceAsStream("functions.bsk"), "skript.functions");
        final PostCompileClass second = skript.compileScript(FunctionsTest.class.getClassLoader()
            .getResourceAsStream("lambda.bsk"), "skript.lambda");
        script = skript.loadScript(cls);
        skript.loadScript(second);
    }
    
    @Test
    public void input() throws Throwable {
        final Member function = script.getFunction("input");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void reflection() throws Throwable {
        final Member function = script.getFunction("reflection");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void external() throws Throwable {
        final Member function = script.getFunction("external");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void skript_default() throws Throwable {
        final Member function = script.getFunction("skript_default");
        assert function != null;
        function.invoke();
    }
    
}
