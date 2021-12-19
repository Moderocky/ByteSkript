package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class FunctionsTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FunctionsTest.class.getClassLoader()
            .getResourceAsStream("functions.bsk"), "skript.functions");
        final PostCompileClass second = skript.compileScript(FunctionsTest.class.getClassLoader()
            .getResourceAsStream("lambda.bsk"), "skript.lambda");
        debug(cls);
        script = skript.loadScript(cls);
        skript.loadScript(second);
    }
    
    @Test
    public void input() throws Throwable {
        final Member function = script.getFunction("input");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void reflection() throws Throwable {
        final Member function = script.getFunction("reflection");
        assert function != null;
        function.invoke();
    }
    
    @Test
    public void external() throws Throwable {
        final Member function = script.getFunction("external");
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
