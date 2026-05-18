/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import org.byteskript.skript.api.syntax.Literal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * May be used to annotate a record component to automatically generate an entry node for the section represented
 * by the record itself. The record component must have a type that can be created from a {@link Literal}.
 * */
@Target({ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntryNode {
    /**
     * Pattern for the syntax element. Must contain exactly one input group, being one that can be created from a literal.
     * */
    String[] value();
    
}
