/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime;

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

public final class Script {
    private final File sourceFile;
    private final Class<?>[] classes;
    private final String name;
    private final Map<String, Member> functions;
    private final List<Member> events;
    private final Collection<SourceData> data;
    private final Structure[] members;
    
    public Script(Skript skript, File sourceFile, Class<?>... classes) {
        this(false, skript, sourceFile, classes);
    }
    
    public Script(boolean init, Skript skript, File sourceFile, Class<?>... classes) {
        this.sourceFile = sourceFile;
        this.classes = classes;
        this.name = mainClass().getName();
        this.functions = new HashMap<>();
        this.events = new ArrayList<>();
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
                this.events.add(member);
                skript.registerEventHandler(event.event(), new InvokingScriptRunner(mainClass(), member));
            }
        }
        this.members = structures.toArray(new Structure[0]);
        if (init) {
            verify();
            load(skript);
        }
    }
    
    void verify() {
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
    }
    
    void load(Skript skript) {
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
    
    public Structure[] getMembers() {
        return members;
    }
    
    private void forceLoad(Class<?> cls) {
        try {
            Class.forName(cls.getName(), true, cls.getClassLoader());
        } catch (ClassNotFoundException ignore) {
        }
    }
    
    
}
