/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.compiler.DebugSkriptCompiler;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.internal.ModifiableCompiler;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SyntaxTreeTest extends SkriptTest {
    
    
    @Test
    public void testRecompile() {
        System.setProperty("byteskript.no_colours", "true");
        final OutputStream stream = new ByteArrayOutputStream();
        final Skript skript = new Skript(new DebugSkriptCompiler(stream));
        final ModifiableCompiler compiler = skript.getCompiler();
        compiler.compile("""
            dictionary:
                import type java/util/Objects
                import function "equals" from java/util/Objects
                use unsafe
                
            function test (a, b):
                trigger:
                    print {a} + {b}
                    loop 5 times:
                        print "hello"
                        
            function test:
                return: string
                trigger:
                    print "hello"
                    
            on script load:
                trigger:
                    print "Foo"
            """, new Type("test"));
        assert stream.toString().equals("""
            
            
            --test
            
            MemberDictionary():
            	EffectImportType(ExprType(java/util/Objects))
            	EffectImportFunction(StringLiteral("equals"), ExprType(java/util/Objects))
            	EffectUseLibrary(unsafe)
             
            MemberFunction(a, b):
            	EntryTriggerSection():
            		EffectPrint(ExprAdd(ExprVariable(a), ExprVariable(b)))
            		EffectLoopTimesSection(IntegerLiteral(5)):
            			EffectPrint(StringLiteral("hello"))
               
            MemberFunctionNoArgs():
            	EntryReturn(string)
            	EntryTriggerSection():
            		EffectPrint(StringLiteral("hello"))
              
            EventLoad():
            	EntryTriggerSection():
            		EffectPrint(StringLiteral("Foo"))""") : '"' + stream.toString() + '"';
    }
    
}
