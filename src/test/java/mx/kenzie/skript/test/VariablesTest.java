package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Future;

public class VariablesTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(VariablesTest.class.getClassLoader()
            .getResourceAsStream("variables.bsk"), "skript.variables");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void basic_variables() throws Throwable {
        final Member function = script.getFunction("basic_variables");
        assert function != null;
        final Future<?> future = function.run(skript);
        future.get();
    }
    
    @Test
    public void atomic_variables() throws Throwable {
        final Member function = script.getFunction("atomic_variables");
        assert function != null;
        final Future<?> future = function.run(skript);
        future.get();
    }
    
    @Test
    public void thread_variables() throws Throwable {
        final Member function = script.getFunction("thread_variables");
        assert function != null;
        final Future<?> future = function.run(skript);
        future.get();
    }
    
    @Test
    public void global_variables() throws Throwable {
        final Member function = script.getFunction("global_variables");
        assert function != null;
        final Future<?> future = function.run(skript);
        future.get();
    }
    
}
