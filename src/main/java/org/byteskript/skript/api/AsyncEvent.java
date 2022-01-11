/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Ignore;

@Ignore
public abstract class AsyncEvent extends Event {
    
    @Override
    public final boolean isAsync() {
        return true;
    }
}
