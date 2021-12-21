package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(EventTest.class.getClassLoader()
            .getResourceAsStream("events.bsk"), "skript.events");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void test() {
    }
    
}
