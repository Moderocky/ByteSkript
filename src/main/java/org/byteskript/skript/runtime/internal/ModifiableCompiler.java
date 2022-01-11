/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Document;
import org.byteskript.skript.api.Library;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Description("""
    This is a template for a modifiable compiler.
    It does not do anything by itself - it is simply a barrier to prevent exceptions in a minimal runtime.
    
    Compilers will already have to implement the majority of these methods from the Compiler interface.
    """)
public interface ModifiableCompiler {
    
    Class<?> load(byte[] bytecode, String name);
    
    PostCompileClass[] compile(InputStream stream, Type name);
    
    PostCompileClass[] compile(InputStream file, String path);
    
    PostCompileClass[] compile(String file, Type path);
    
    boolean addLibrary(Library library);
    
    boolean removeLibrary(Library library);
    
    @Description("""
        Generates the totality of syntax documentation from provided libraries.
        """)
    default Document[] generateDocumentation() {
        final List<Document> documents = new ArrayList<>();
        for (final Library library : getLibraries()) {
            documents.addAll(Arrays.asList(library.generateDocumentation()));
        }
        return documents.toArray(new Document[0]);
    }
    
    Library[] getLibraries();
    
}
