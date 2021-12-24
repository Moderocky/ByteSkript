package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;

import java.lang.reflect.Method;

public record PropertyHandler(String name, HandlerType type, Type holder, Type value, Method method) {
}
