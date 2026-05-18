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
 * May be used to annotate a method to automatically declare it as an expression. The code of the method is executed
 * when the expression is used.
 * <p>
 * For each input group in the expression's pattern, the method must have a corresponding
 * method parameter; otherwise, the operand stack will destabilise, causing difficult-to-identify bugs.
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {
    /**
     * The pattern to use for the expression.
     * */
    String[] value();
    
}
