package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class FlowTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("flow.bsk"), "skript.flow");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void sleep_flow() throws Throwable {
        final Member function = script.getFunction("sleep_flow");
        assert function != null;
        function.run(skript);
    }
    
    @Test
    public void if_flow() throws Throwable {
        final Member function = script.getFunction("if_flow");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void loop_flow() throws Throwable {
        final Member function = script.getFunction("loop_flow");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void while_flow() throws Throwable {
        final Member function = script.getFunction("while_flow");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void inline_headers() throws Throwable {
        final Member function = script.getFunction("inline_headers");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void test_run() throws Throwable {
        final Member function = script.getFunction("test_run");
        assert function != null;
        function.invoke();
    }
    
}
