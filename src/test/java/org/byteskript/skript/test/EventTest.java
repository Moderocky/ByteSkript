/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.runtime.Skript;

import java.util.concurrent.ExecutionException;

public final class EventTest extends SkriptTest {
    private static final Skript SKRIPT = new Skript();
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
		SKRIPT.registerLibrary(new ModifiableLibrary("test") {{
			generateSyntaxFrom(TestEvent.class);
		}});
        final PostCompileClass postCompileClass = SKRIPT.compileScript(
                """
					on test:
						trigger:
							print "Start"
							wait 1 second
							print "Finish"
					""", "skript.events");
	    SKRIPT.loadScript(postCompileClass);
        final long now = System.currentTimeMillis();
		SKRIPT.runEvent(new TestEvent())
				.orElseThrow()
				.thenRun(() -> {
					final long took = System.currentTimeMillis() - now;
					System.out.println("Took " + took + "ms.");
				})
				.get();
    }
	
	@org.byteskript.skript.api.note.Event("on test")
	private static final class TestEvent extends Event {}
}
