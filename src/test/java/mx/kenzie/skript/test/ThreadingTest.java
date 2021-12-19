package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ThreadingTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("thread.bsk"), "skript.threading");
        debug(cls);
        script = skript.loadScript(cls);
        final Member function = script.getFunction("test");
        assert function != null;
        new ExampleController(skript).run();
    }
    
    public static void main(String[] args) throws Throwable {
        start();
        System.out.println("Finished");
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
