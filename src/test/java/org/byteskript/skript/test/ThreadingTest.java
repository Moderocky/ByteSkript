/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.api.note.EventValue;
import org.byteskript.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

public class ThreadingTest extends SkriptTest {
    
    private static final Skript skript
        = new Skript();
    //    = new Skript(new DebugSkriptCompiler(Stream.controller(System.out)));
    private static PostCompileClass cls;
    private final String test = """
        on test:
            trigger:
                print "Start"
                wait 1 second
                print "Finish"
                
        """;
    
    @BeforeClass
    public static void setup() {
        skript.registerLibrary(new ModifiableLibrary("test") {{ // Credit: kiip1 for suggestion.
            generateSyntaxFrom(TestEvent.class);
        }});
        cls = skript.compileScript(
            """
                on test:
                    trigger:
                        wait 1 millisecond
                        set event-thing to 6
                """, "skript.events");
        skript.loadScript(cls);
    }
    
    @Test
    public void eventSynchronized() throws Throwable {
        final TestEvent event;
        skript.runEvent(event = new TestEvent())
            .all()
            .get();
        assert event.number == 6;
    }
    
    @org.byteskript.skript.api.note.Event("on test")
    public static final class TestEvent extends Event {
        public int number = 1;
        
        @EventValue("thing")
        public void setThing(Number number) {
            this.number = (int) number;
        }
    }
}
