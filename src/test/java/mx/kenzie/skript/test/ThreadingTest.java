package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ThreadingTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    public static void main(String[] args) throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("flow.bsk"), "skript.flow");
        debug(cls);
        script = skript.loadScript(cls);
        final Member function = script.getFunction("sleep_flow");
        assert function != null;
        function.run(skript);
    }
    
    
    private static void debug(final PostCompileClass source) throws Throwable {
        try (OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
