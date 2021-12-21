package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.runtime.Script;
import mx.kenzie.skript.runtime.Skript;
import mx.kenzie.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class LambdaTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        final PostCompileClass cls = skript.compileScript(LambdaTest.class.getClassLoader()
            .getResourceAsStream("lambda.bsk"), "skript.lambda");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void lambda_test() throws Throwable {
        final Member function = script.getFunction("lambda_test");
        assert function != null;
        function.invoke();
    }
    
}
