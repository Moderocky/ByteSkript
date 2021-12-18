package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class BasicCompilationTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(BasicCompilationTest.class.getClassLoader()
            .getResourceAsStream("map.bsk"), "skript.test");
        debug(cls);
        script = skript.loadScript(cls);
    }
    
    @Test
    public void test_method() throws Throwable {
        final Method function = script.getFunction("test_method");
        assert function != null;
        function.invoke(null);
    }
    
    @Test
    public void my_function() throws Throwable {
        final Method function = script.getFunction("my_function");
        assert function != null;
        function.invoke(null);
    }
    
    @Test
    public void testing_variable() throws Throwable {
        final Method function = script.getFunction("testing_variable");
        assert function != null;
        function.invoke(null);
    }
    
    @Test
    public void testing_map() throws Throwable {
        final Method function = script.getFunction("testing_map");
        assert function != null;
        function.invoke(null);
    }
    
    @Test
    public void run_function() throws Throwable {
        final Method function = script.getFunction("run_function");
        assert function != null;
        assert "bees".equals(function.invoke(null));
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
