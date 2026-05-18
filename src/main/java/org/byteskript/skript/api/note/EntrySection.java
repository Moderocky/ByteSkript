/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import org.byteskript.skript.api.FunctionalEntrySection;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * May be used to annotate a record class to automatically generate a {@link Section} corresponding to the record class.
 * Components of the record should be annotated with {@link EntryNode} to generate a corresponding entry node.
 * <p>
 * If the annotated record is a {@link FunctionalEntrySection}, its {@link FunctionalEntrySection#compile(Context)} method
 * will be called when the record is being compiled, which allows for custom compilation steps with access to the parsed
 * literal values.
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntrySection {
    /**
     * The pattern to use for the entry section.
     * */
    String[] value();
    
}
