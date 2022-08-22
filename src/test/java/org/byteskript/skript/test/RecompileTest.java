package org.byteskript.skript.test;

import org.byteskript.skript.runtime.*;
import org.byteskript.skript.runtime.event.Load;
import org.junit.Test;

import java.io.*;

public class RecompileTest extends SkriptTest {

    private static final Skript skript = new Skript();

    private static Script loadSync(final String source) {
        final Script script = skript.assembleScript(
            skript.compileScript(source, "skript.recompilation.bsk"));
        skript.runEvent(new Load.LoadThis(script), script).all().join();
        return script;
    }

    @Test
    public void testRecompile() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        skript.setOutput(new PrintStream(output));

        Script script = loadSync("""
                on script load:
                    trigger:
                        print "Foo"
                """);
        skript.unloadScript(script);

        script = loadSync("""
                on script load:
                    trigger:
                        print "Bar"
                """);
        skript.unloadScript(script);

        assert output.toString().equals("Foo\nBar\n");
    }

}