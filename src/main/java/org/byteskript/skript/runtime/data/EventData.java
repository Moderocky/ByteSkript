/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.data;

import mx.kenzie.autodoc.api.note.Ignore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Ignore
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventData {
    
    String name();
    
    String event();
    
    boolean async();
    
}
