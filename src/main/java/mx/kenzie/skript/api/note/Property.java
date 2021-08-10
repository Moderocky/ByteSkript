package mx.kenzie.skript.api.note;

import mx.kenzie.skript.lang.handler.StandardHandlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    
    String value();
    
    StandardHandlers type() default StandardHandlers.GET;
    
}
