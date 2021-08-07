package mx.kenzie.skript.api;

public interface Instruction<Type> {
    
    void run() throws Throwable;
    
    Type get();
    
}
