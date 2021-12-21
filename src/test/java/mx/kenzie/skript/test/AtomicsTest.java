package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class AtomicsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(AtomicsTest.class.getClassLoader()
            .getResourceAsStream("atomics.bsk"), "skript.atomics");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void basic_use() throws Throwable {
        final Member function = script.getFunction("basic_use");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void passed_use() throws Throwable {
        final Member function = script.getFunction("passed_use");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void as_simple_parameter() throws Throwable {
        final Member function = script.getFunction("as_simple_parameter");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void as_atomic_parameter() throws Throwable {
        final Member function = script.getFunction("as_atomic_parameter");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void reverse_case() throws Throwable {
        final Member function = script.getFunction("reverse_case");
        assert function != null;
        function.invoke();
    }
    
}
