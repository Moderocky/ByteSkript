/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for documenting syntax element classes.
 * */
@Retention(RetentionPolicy.RUNTIME)
public @interface Documentation {

    /**
     * The user-friendly name of this syntax element.
     * */
    String name() default "";

    /**
     * A helpful description of this syntax element.
     * */
    String description() default "";

    /**
     * An array of code blocks showing example usages of this syntax element.
     * */
    String[] examples() default {};
    
}
