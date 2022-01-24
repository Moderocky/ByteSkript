/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Example;
import mx.kenzie.autodoc.api.note.GenerateExample;
import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.error.ScriptLoadError;
import org.byteskript.skript.runtime.data.EventData;
import org.byteskript.skript.runtime.data.Function;
import org.byteskript.skript.runtime.data.SourceData;
import org.byteskript.skript.runtime.data.Structure;
import org.byteskript.skript.runtime.event.Load;
import org.byteskript.skript.runtime.internal.CompiledScript;
import org.byteskript.skript.runtime.internal.InvokingScriptRunner;
import org.byteskript.skript.runtime.internal.Member;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

@Description("""
    A handle representation of a script containing its metadata and defined classes.
    
    This can be used to find insights about a script or find a particular function handle.
    
    These should not be stored - they can be arbitrarily graveyarded without any notification
    by the unloader, in which case this will become a dead spot in memory.
    """)
@Example("""
    final PostCompileClass data = skript.compileScript(code, "skript.myscript");
    final Script script = skript.loadScript(data);
    final Member function = script.getFunction("my_func");
    function.run();
    """)
@Example("""
    final PostCompileClass data = skript.compileScript(code, "skript.myscript");
    final Script script = skript.loadScript(data);
    skript.runEvent(new MyEvent(), script);
    """)
public final class Script {
    private final File sourceFile;
    private final Class<?>[] classes;
    private final String name;
    private final Map<String, Member> functions;
    private final Collection<SourceData> data;
    private final Structure[] members;
    private final Skript skript;
    
    @Ignore
    Script(Skript skript, File sourceFile, Class<?>... classes) {
        this(true, skript, sourceFile, classes);
    }
    
    @Ignore
    @SuppressWarnings("unchecked")
    Script(boolean init, Skript skript, File sourceFile, Class<?>... classes) {
        this.skript = skript;
        this.sourceFile = sourceFile;
        this.classes = classes;
        this.name = mainClass().getName();
        this.functions = new HashMap<>();
        this.data = new ArrayList<>();
        final List<Structure> structures = new ArrayList<>();
        for (final Class<?> type : classes) {
            if (!type.isAnnotationPresent(SourceData.class)) continue;
            final SourceData data = type.getAnnotation(SourceData.class);
            structures.add(new Structure(data.type(), data.name(), type));
        }
        for (Method method : mainClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SourceData.class)) {
                final SourceData data = method.getDeclaredAnnotation(SourceData.class);
                structures.add(new Structure(data.type(), data.name(), method));
                this.data.add(data);
            }
            if (method.isAnnotationPresent(Function.class)) {
                final Function function = method.getAnnotation(Function.class);
                this.functions.put(function.name(), new Member(this, method, function.async()));
            } else if (method.isAnnotationPresent(EventData.class)) {
                final EventData event = method.getAnnotation(EventData.class);
                final Member member = new Member(this, method, event.async());
                skript.registerEventHandler((Class<? extends Event>) skript.getClass(event.event()), new InvokingScriptRunner(mainClass(), member));
            }
        }
        this.members = structures.toArray(new Structure[0]);
        if (init) {
            this.verify();
            this.load(skript);
        }
    }
    
    @Description("""
        Gets the main class of this script, which root-level members occupy.
        Custom types and other members may be moved to other classes, depending on the compiler used.
        
        This should not be stored - it will prevent the script unloading safely.
        """)
    public Class<? extends CompiledScript> mainClass() {
        return (Class<? extends CompiledScript>) classes[0];
    }
    
    @Ignore
    void verify() {
        this.forceLoad(this.mainClass());
        for (final Map.Entry<String, Member> entry : functions.entrySet()) {
            final Member value = entry.getValue();
            final String name = entry.getKey();
            try {
                value.verify();
            } catch (Throwable ex) {
                throw new ScriptLoadError("Function '" + name + "' failed verification.", ex);
            }
        }
    }
    
    @Ignore
    void load(Skript skript) {
        skript.runEvent(new Load.LoadThis(this), this);
        skript.runEvent(new Load(this));
    }
    
    private void forceLoad(Class<?> cls) {
        try {
            Class.forName(cls.getName(), true, cls.getClassLoader());
        } catch (ClassNotFoundException ignore) {
        }
    }
    
    @Description("""
        The simple name of the main class for this script.
        This will be something in the format `script`.
        """)
    @GenerateExample
    public String getSimpleName() {
        return mainClass().getSimpleName();
    }
    
    @Description("""
        The path of the main class for this script.
        This will be something in the format `skript.path.to.script`
        """)
    @Example("assert script.getPath().startsWith(myPathName);")
    public String getPath() {
        return mainClass().getName();
    }
    
    @Description("Whether this script has a known source file.")
    @GenerateExample
    public boolean hasSourceFile() {
        return sourceFile != null && sourceFile.exists() && sourceFile.isFile();
    }
    
    @Description("""
        Returns a handle for the function with this name.
        Multiple functions may have the same name, this will return an arbitrary one.
        """)
    @GenerateExample
    public Member getFunction(String name) {
        return functions.get(name);
    }
    
    @Description("Finds source data annotations for members in this script.")
    public Collection<SourceData> getMemberData() {
        return data;
    }
    
    @Description("""
        Returns the known source file for this script.
        This may not exist for some implementations.
        """)
    @GenerateExample
    public File sourceFile() {
        return sourceFile;
    }
    
    @Description("""
        Returns the known classes for this script.
        This will include the main class and any custom types, etc.
        """)
    public Class<?>[] classes() {
        return classes;
    }
    
    @Override
    @Ignore
    public String toString() {
        return "Script[" +
            "name=" + name + ']';
    }
    
    @Description("Returns the structures for all members, used for data collection.")
    public Structure[] getMembers() {
        return members;
    }
    
    @Description("Returns the Skript runtime that created this script.")
    @GenerateExample
    public Skript skriptInstance() {
        return skript;
    }
}
