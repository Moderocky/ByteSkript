package mx.kenzie.skript.runtime.internal;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.skript.api.Library;

import java.io.InputStream;

public interface ModifiableCompiler {
    
    Class<?> load(byte[] bytecode, String name);
    
    PostCompileClass[] compile(InputStream stream, Type name);
    
    PostCompileClass[] compile(InputStream file, String path);
    
    PostCompileClass[] compile(String file, Type path);
    
    boolean addLibrary(Library library);
    
    boolean removeLibrary(Library library);
    
}
