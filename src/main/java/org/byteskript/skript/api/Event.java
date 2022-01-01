/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import org.byteskript.skript.runtime.Skript;

public abstract class Event {
    
    public final void run(final Skript skript) {
        skript.runEvent(this);
    }
    
    public boolean isAsync() {
        return false;
    }
    
}
