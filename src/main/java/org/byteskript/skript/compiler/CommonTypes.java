/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.runtime.config.ConfigMap;
import org.byteskript.skript.runtime.type.AtomicVariable;
import org.byteskript.skript.runtime.type.Executable;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CommonTypes {
    
    public static final Type STRING = new Type(String.class);
    public static final Type OBJECT = new Type(Object.class);
    public static final Type OBJECTS = new Type(Object[].class);
    public static final Type BOOLEAN = new Type(Boolean.class);
    public static final Type INTEGER = new Type(Integer.class);
    public static final Type DOUBLE = new Type(Double.class);
    public static final Type FLOAT = new Type(Float.class);
    public static final Type LONG = new Type(Long.class);
    public static final Type NUMBER = new Type(Number.class);
    public static final Type CLASS = new Type(Class.class);
    public static final Type TYPE = new Type(java.lang.reflect.Type.class);
    public static final Type DURATION = new Type(Duration.class);
    public static final Type VOID = new Type(Void.class);
    public static final Type EVENT = new Type(Event.class);
    public static final Type THREAD = new Type(Thread.class);
    
    public static final Type THROWABLE = new Type(Throwable.class);
    public static final Type ERROR = new Type(Error.class);
    
    public static final Type LIST = new Type(List.class);
    public static final Type MAP = new Type(Map.class);
    public static final Type CONFIG = new Type(ConfigMap.class);
    public static final Type REFERENT = new Type(Referent.class);
    public static final Type RUNNABLE = new Type(Runnable.class);
    public static final Type SUPPLIER = new Type(Supplier.class);
    public static final Type EXECUTABLE = new Type(Executable.class);
    
    public static final Type ATOMIC = new Type(AtomicVariable.class);
    public static final Type METHOD = new Type(Method.class);
    public static final Type FIELD = new Type(Field.class);
    public static final Type INPUT_STREAM = new Type(InputStream.class);
    public static final Type OUTPUT_STREAM = new Type(OutputStream.class);
    
}
