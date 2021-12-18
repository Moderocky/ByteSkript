package mx.kenzie.skript.api.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forces a method's raw bytecode to be extracted into the compiled class.
 * This should be used sparingly. It is natively unsafe.
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceBridge {
}
