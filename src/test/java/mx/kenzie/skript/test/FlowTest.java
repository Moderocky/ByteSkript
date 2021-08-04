package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class FlowTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("flow.bsk"), "skript.flow");
        debug(cls);
        script = skript.loadScript(cls);
    }
    
    @Test
    public void test_method() throws Throwable {
        final Method function = script.getFunction("if_flow");
        assert function != null;
        function.invoke(null);
    }
    
    @Test
    public void while_flow() throws Throwable {
        final Method function = script.getFunction("while_flow");
        assert function != null;
        function.invoke(null);
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
