/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.type;

import mx.kenzie.autodoc.api.note.Ignore;

@Ignore
@FunctionalInterface
public interface Query {
    
    boolean check(Object value);
    
}
