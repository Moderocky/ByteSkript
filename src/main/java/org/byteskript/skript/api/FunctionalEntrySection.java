/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.compiler.Context;

@Ignore
public interface FunctionalEntrySection {
    
    void compile(Context context) throws Throwable;
    
}
