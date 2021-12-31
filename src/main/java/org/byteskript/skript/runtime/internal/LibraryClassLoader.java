/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryClassLoader extends URLClassLoader {
    
    public LibraryClassLoader(File file, ClassLoader parent) throws IOException {
        super(new URL[]{file.toURI().toURL()}, parent);
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
    
}
