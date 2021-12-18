package mx.kenzie.skript.runtime;

import mx.kenzie.skript.lang.event.Load;
import mx.kenzie.skript.runtime.data.EventData;
import mx.kenzie.skript.runtime.data.Function;
import mx.kenzie.skript.runtime.data.SourceData;
import mx.kenzie.skript.runtime.internal.CompiledScript;
import mx.kenzie.skript.runtime.internal.InvokingScriptRunner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public final class Script {
    private final File sourceFile;
    private final Class<?>[] classes;
    private final String name;
    private final Map<String, Method> functions;
    private final List<Method> events;
    private final Collection<SourceData> data;
    
    public Script(Skript skript, File sourceFile, Class<?>... classes) {
        this.sourceFile = sourceFile;
        this.classes = classes;
        this.name = mainClass().getName();
        this.functions = new HashMap<>();
        this.events = new ArrayList<>();
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
            event:
            {
                final EventData event = method.getAnnotation(EventData.class);
                if (event == null) break event;
                this.events.add(method);
                skript.registerEventHandler(event.event(), new InvokingScriptRunner(mainClass(), method));
            }
        }
        forceLoad(mainClass());
        skript.runEvent(new Load.LoadThis(this), this);
        skript.runEvent(new Load(this));
    }
    
    public String getSimpleName() {
        return mainClass().getSimpleName();
    }
    
    public String getPath() {
        return mainClass().getName();
    }
    
    public Class<? extends CompiledScript> mainClass() {
        return (Class<? extends CompiledScript>) classes[0];
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
    
    private void forceLoad(Class<?> cls) {
        try {
            Class.forName(cls.getName(), true, cls.getClassLoader());
        } catch (ClassNotFoundException ignore) {
        }
    }
    
    
}
