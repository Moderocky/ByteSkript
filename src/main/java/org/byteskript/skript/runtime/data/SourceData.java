/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.data;

import mx.kenzie.autodoc.api.note.Ignore;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Ignore
@Retention(RetentionPolicy.RUNTIME)
public @interface SourceData {
    
    int line();
    
    String type() default "unknown";
    
    String name() default "unknown";
    
    String file() default "Unknown";
    
    long compiled();
    
}
