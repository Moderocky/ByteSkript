package mx.kenzie.skript.runtime;

import mx.kenzie.skript.runtime.data.Function;
import mx.kenzie.skript.runtime.data.SourceData;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Script {
    private final File sourceFile;
    private final Class<?>[] classes;
    private final String name;
    private final Map<String, Method> functions;
    private final Collection<SourceData> data;
    
    public Script(File sourceFile, Class<?>... classes) {
        this.sourceFile = sourceFile;
        this.classes = classes;
        this.name = mainClass().getName();
        this.functions = new HashMap<>();
        this.data = new ArrayList<>();
        for (Method method : mainClass().getDeclaredMethods()) {
            source:
            {
                final SourceData data = method.getDeclaredAnnotation(SourceData.class);
                if (data == null) break source;
                this.data.add(data);
            }
            function:
            {
                final Function function = method.getAnnotation(Function.class);
                if (function == null) break function;
                this.functions.put(function.name(), method);
            }
        }
    }
    
    public Class<?> mainClass() {
        return classes[0];
    }
    
    public boolean hasSourceFile() {
        return sourceFile != null && sourceFile.exists() && sourceFile.isFile();
    }
    
    public Method getFunction(String name) {
        return functions.get(name);
    }
    
    public Collection<SourceData> getMemberData() {
        return data;
    }
    
    public File sourceFile() {
        return sourceFile;
    }
    
    public Class<?>[] classes() {
        return classes;
    }
    
    @Override
    public String toString() {
        return "Script[" +
            "name=" + name + ']';
    }
    
    
}
