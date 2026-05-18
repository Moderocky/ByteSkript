/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.note;

import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.generic.ExprProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * May be used to annotate a method to automatically declare it as a property, to be accessed using {@link ExprProperty}.
 * <p>
 * A property may be declared with any name, but only properties with names that are valid ByteSkript identifiers are usable in code.
 * <p>
 * The annotated method should have a signature that matches the declared {@link Property#type()}. The method must always be static
 * and have its first parameter corresponding to the type of the object that it is a property for. For {@link StandardHandlers handlers} that accept input,
 * the second parameter will be the input.
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    /**
     * The name of the property, which should be a valid ByteSkript identifier.
     * */
    String value();

    /**
     * The type of the property handler.
     * */
    StandardHandlers type() default StandardHandlers.GET;
    
}
