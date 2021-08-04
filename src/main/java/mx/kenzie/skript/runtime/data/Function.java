package mx.kenzie.skript.runtime.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Function {
    
    String name();
    
    int arguments();
    
    boolean async();
    
}
