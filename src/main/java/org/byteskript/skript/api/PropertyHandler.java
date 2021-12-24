/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;

import java.lang.reflect.Method;

public record PropertyHandler(String name, HandlerType type, Type holder, Type value, Method method) {
}
