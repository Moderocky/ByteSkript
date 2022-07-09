/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.mirror.ClassProvider;
import org.byteskript.skript.runtime.Skript;

import java.util.ArrayList;
import java.util.List;

/**
 * This class loader aims to fix a hierarchy issue where Java's class-loader
 * simply cannot identify classes it has loaded.
 * <p>
 * I was unable to triage the cause of this issue, though I would guess it is
 * something to do with a lack of synchronization.
 */
@Ignore
public class ScriptClassLoader extends ClassLoader implements ClassProvider {
    
    final List<Class<?>> loaded = new ArrayList<>();
    
    public ScriptClassLoader() {
        super(Skript.findLoader());
    }
    
    public Class<?> loadClass0(String name) throws ClassNotFoundException {
        for (final Class<?> thing : loaded) if (thing.getName().equals(name)) return thing;
        return super.loadClass(name);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (final Class<?> thing : loaded) if (thing.getName().equals(name)) return thing;
        return Skript.findLoader().loadClass(name);
    }
    
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        for (final Class<?> thing : loaded) if (thing.getName().equals(name)) return thing;
        return Skript.findLoader().findClass(name);
    }
    
    public Class<?> findClass0(String name) throws ClassNotFoundException {
        for (final Class<?> thing : loaded) if (thing.getName().equals(name)) return thing;
        return super.findClass(name);
    }
    
    @Override
    public Class<?> loadClass(Class<?> aClass, String s, byte[] bytes) {
        final Class<?> thing = this.defineClass(s, bytes, 0, bytes.length);
        this.loaded.add(thing);
        return thing;
    }
    
    @Override
    public String toString() {
        return "ScriptClassLoader{" +
            "loaded=" + loaded +
            '}';
    }
}
