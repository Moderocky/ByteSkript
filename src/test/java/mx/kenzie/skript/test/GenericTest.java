package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class GenericTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(GenericTest.class.getClassLoader()
            .getResourceAsStream("generic.bsk"), "skript.test");
        debug(cls);
        script = skript.loadScript(cls);
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
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (final OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
