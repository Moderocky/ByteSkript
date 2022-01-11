/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.data;

import mx.kenzie.autodoc.api.note.Ignore;

import java.lang.reflect.AnnotatedElement;

@Ignore
public record Structure(String type, String name, AnnotatedElement element) {
}
