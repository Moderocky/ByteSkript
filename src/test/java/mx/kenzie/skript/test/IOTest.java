package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class IOTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(IOTest.class.getClassLoader()
            .getResourceAsStream("io.bsk"), "skript.io");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void file() throws Throwable {
        final Member function = script.getFunction("file");
        assert function != null;
        function.invoke();
    }
    
}
