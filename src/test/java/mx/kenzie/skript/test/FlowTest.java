package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

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
    public void if_flow() throws Throwable {
        final Member function = script.getFunction("if_flow");
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
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
