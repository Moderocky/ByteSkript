/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import org.byteskript.skript.runtime.internal.IOHandlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * May be used to annotate a method to automatically generate a corresponding effect. The annotated method must have
 * a non-void return type and must accept parameters for each input group in the syntax pattern.
 *
 * @see IOHandlers Usage examples in IOHandlers
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Effect {
    /**
     * The syntax pattern to use for the element.
     * */
    String[] value();
    
}
