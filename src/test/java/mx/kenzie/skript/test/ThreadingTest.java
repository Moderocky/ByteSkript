package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class ThreadingTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(ProvidedFunctionsTest.class.getClassLoader()
            .getResourceAsStream("flow.bsk"), "skript.flow");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void sleep_test() throws Throwable {
        final Member function = script.getFunction("sleep_flow");
        assert function != null;
        function.run(skript).get();
    }
    
}
