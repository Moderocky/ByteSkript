package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class SectionsTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SectionsTest.class.getClassLoader()
            .getResourceAsStream("sections.bsk"), "skript.sections");
        debug(cls);
        script = skript.loadScript(cls);
    }
    
    @Test
    public void relay_test() throws Throwable {
        script.getFunction("relay_test").invoke();
    }
    
    public static Object blob(String thing) {
        return "hello " + thing;
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
