package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

public class SectionsTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(SectionsTest.class.getClassLoader()
            .getResourceAsStream("sections.bsk"), "skript.sections");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void relay_test() throws Throwable {
        script.getFunction("relay_test").invoke();
    }
    
    public static Object blob(String thing) {
        return "hello " + thing;
    }
    
}
