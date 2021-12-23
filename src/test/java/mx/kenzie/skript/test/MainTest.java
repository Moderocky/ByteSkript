package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;

public class MainTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    public static void main(String[] args) throws Throwable {
        final PostCompileClass cls = skript.compileScript(FlowTest.class.getClassLoader()
            .getResourceAsStream("main.bsk"), "skript.main");
        script = skript.loadScript(cls);
        new ExampleController(skript).run();
        System.out.println("Finished.");
    }
    
}
