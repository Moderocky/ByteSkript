/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.data;

import java.lang.reflect.AnnotatedElement;

public record Structure(String type, String name, AnnotatedElement element) {
}
