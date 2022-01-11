/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Description("""
    Denotes behaviour that is dependent on a specific compiler being available.
    """)
@Retention(RetentionPolicy.CLASS)
public @interface CompilerDependent {
}
