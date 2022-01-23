/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.Member;
import org.junit.BeforeClass;
import org.junit.Test;

public class DictionaryTest extends SkriptTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    private static Script remote;
    
    @BeforeClass
    public static void start() throws Throwable {
        remote = skript.compileLoad(GenericTest.class.getClassLoader()
            .getResourceAsStream("generic.bsk"), "skript.generic");
        final PostCompileClass cls = skript.compileScript(GenericTest.class.getClassLoader()
            .getResourceAsStream("dictionary.bsk"), "skript.dictionary");
        script = skript.loadScript(cls);
    }
    
    @Test
    public void dictionary() throws Throwable {
        final Member function = script.getFunction("test");
        assert function != null;
        function.invoke();
    }
    
}
