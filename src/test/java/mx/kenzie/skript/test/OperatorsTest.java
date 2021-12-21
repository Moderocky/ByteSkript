package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
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
