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
 * May be used to annotate methods of an event class to declare event values for the event.
 * <p>
 * A setter may be declared by annotating with this annotation a method that returns <code>void</code> and accepts
 * one parameter (i.e. the value to set the event value to).
 * <p>
 * A getter may be declared by annotating with this annotation a method that <i>does not</i> return <code>void</code> nor
 * accept any parameters. It should return the corresponding value for the event.
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventValue {
    /**
     * The name to use for accessing the event value in a script, e.g. "player" for <code>event-player</code>.
     * */
    String value();
    
}
