package org.byteskript.skript.test;

import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.event.Load;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RecompileTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    
    @Test
    public void testRecompile() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        skript.setOutput(new PrintStream(output));
        final Script first = RecompileTest.loadSync("""
            on script load:
                trigger:
                    print "Foo"
            """);
        skript.unloadScript(first);
        final Script second = RecompileTest.loadSync("""
            on script load:
                trigger:
                    print "Bar"
            """);
        skript.unloadScript(second);
        assert output.toString().equals("Foo\nBar\n") : output.toString();
    }
    
    private static Script loadSync(final String source) {
        final Script script = skript.assembleScript(
            skript.compileScript(source, "skript.recompilation.bsk").source());
        skript.runEvent(new Load.LoadThis(script), script).all().join();
        return script;
    }
    
}
