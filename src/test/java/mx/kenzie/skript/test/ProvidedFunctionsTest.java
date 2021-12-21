package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ProvidedFunctionsTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(ProvidedFunctionsTest.class.getClassLoader()
            .getResourceAsStream("provided_functions.bsk"), "skript.provided_functions");
        debug(cls);
        script = skript.loadScript(cls);
    }
    
    @Test
    public void generic() throws Throwable {
        final Member function = script.getFunction("generic");
        assert function != null;
        function.invoke(skript);
    }
    
    @Test
    public void handles() throws Throwable {
        final Member function = script.getFunction("handles");
        assert function != null;
        function.invoke(skript);
    }
    
    @Test
    public void maths() throws Throwable {
        final Member function = script.getFunction("maths");
        assert function != null;
        function.invoke(skript);
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
