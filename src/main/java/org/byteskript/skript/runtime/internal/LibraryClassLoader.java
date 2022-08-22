/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

@Description("""
    The class-loader used by libraries.
    This can be searched by script class-loaders.
    """)
public class LibraryClassLoader extends URLClassLoader {
    
    @Ignore
    public LibraryClassLoader(File file, ClassLoader parent) throws IOException {
        super(new URL[] {file.toURI().toURL()}, parent);
    }
    
    @Ignore
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
    
}
