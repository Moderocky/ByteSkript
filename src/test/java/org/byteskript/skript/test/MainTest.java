/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;

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
