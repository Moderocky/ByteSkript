package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class ErrorHandlingTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        skript.registerLibrary(new TestingLibrary());
        final PostCompileClass cls = skript.compileScript(new ByteArrayInputStream("""
            function test_error:
                trigger:
                    set {var} to 1
                    print "hello"
                    throw exception
            """.getBytes(StandardCharsets.UTF_8)), "skript.error_test");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void basic_use() throws Throwable {
        final Member function = script.getFunction("test_error");
        assert function != null;
        function.invoke();
    }
    
}
