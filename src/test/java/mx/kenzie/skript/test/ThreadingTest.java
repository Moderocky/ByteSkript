package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class ThreadingTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("thread.bsk"), "skript.threading");
        debug(cls);
        script = skript.loadScript(cls);
    }
    
    public static void main(String[] args) throws Throwable {
        start();
        final Method function = script.getFunction("test");
        assert function != null;
        final Thread thread = skript.runScript(function);
        thread.start();
        new ExampleController(skript).run();
        System.out.println("hi");
    }
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}