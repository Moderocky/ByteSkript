/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * May be used to annotate an event class to automatically declare an event holder for it.
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    /**
     * The pattern to use for this event. By convention, event patterns start with "on": <code>on script load</code>.
     * */
    String[] value();
    
}
