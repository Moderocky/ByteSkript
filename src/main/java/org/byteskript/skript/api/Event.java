/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import org.byteskript.skript.runtime.Skript;

@Description("""
    Custom events must extend this class.
    
    The extending version should add any supported event values, and must be registered with the owning library.
    New instances of the event are created each time the event needs to be triggered.
    """)
@Example("""
    public class MyEvent extends Event {
        
        public MyEvent() {
        }
        
        @EventValue("blob") // event-blob
        public String getBlob() {
            return "blob";
        }
        
        @EventValue("lettuce") // event-lettuce
        public Object getLettuce() {
            return null;
        }
    """)
@Example("""
    final MyEvent event = new MyEvent();
    // these two are both valid ways to trigger the event
    event.run(skript);
    skript.runEvent(event);
    // for triggering handlers in only one script
    skript.runEvent(event, script);
    """)
public abstract class Event {
    
    @Description("This is a simple helper method for triggering an event.")
    public final void run(final Skript skript) {
        skript.runEvent(this);
    }
    
    @Description("""
        This has no purpose currently - all event-handlers run on separate processes.
        
        In the future, this may be used to determine whether a thread
        should be reused for multiple handlers of the same event or not.
        
        For now, it can be ignored.
        """)
    public boolean isAsync() {
        return false;
    }
    
}
