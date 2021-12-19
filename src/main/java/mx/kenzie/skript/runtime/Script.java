package mx.kenzie.skript.runtime;

import mx.kenzie.skript.error.ScriptLoadError;
import mx.kenzie.skript.lang.event.Load;
import mx.kenzie.skript.runtime.data.EventData;
import mx.kenzie.skript.runtime.data.Function;
import mx.kenzie.skript.runtime.data.SourceData;
import mx.kenzie.skript.runtime.internal.CompiledScript;
import mx.kenzie.skript.runtime.internal.InvokingScriptRunner;
import mx.kenzie.skript.runtime.internal.Member;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public final class Script {
    private final File sourceFile;
    private final Class<?>[] classes;
    private final String name;
    private final Map<String, Member> functions;
    private final List<Member> events;
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
                this.functions.put(function.name(), new Member(this, method, function.async()));
            }
            event:
            {
                final EventData event = method.getAnnotation(EventData.class);
                if (event == null) break event;
                final Member member = new Member(this, method, event.async());
                this.events.add(member);
                skript.registerEventHandler(event.event(), new InvokingScriptRunner(mainClass(), member));
            }
        }
        forceLoad(mainClass());
        for (Map.Entry<String, Member> entry : functions.entrySet()) {
            final Member value = entry.getValue();
            final String name = entry.getKey();
            try {
                value.verify();
            } catch (Throwable ex) {
                throw new ScriptLoadError("Function '" + name + "' failed verification.", ex);
            }
        }
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
    
    public Member getFunction(String name) {
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
