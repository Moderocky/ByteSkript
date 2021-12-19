package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class OperatorsTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(OperatorsTest.class.getClassLoader()
            .getResourceAsStream("maths.bsk"), "skript.maths");
        debug(cls);
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
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
