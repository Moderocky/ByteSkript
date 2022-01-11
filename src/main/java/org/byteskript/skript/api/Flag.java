/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;

@Description("""
    Compiler flags to provide trivial information to the matcher.
    This should be implemented by an enum.
    """)
public interface Flag {
    
    String name();
    
}
