/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;

import java.util.Objects;

public interface DebugTypeMeta {
    
    default String debug(Object meta) {
        if (meta instanceof Type type) return type.getSimpleName();
        return Objects.toString(meta);
    }
    
}
