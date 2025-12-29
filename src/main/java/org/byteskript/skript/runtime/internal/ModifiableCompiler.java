/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.Document;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.resource.Resource;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.OperatorFunction;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Description("""
    This is a template for a modifiable compiler.
    It does not do anything by itself - it is simply a barrier to prevent exceptions in a minimal runtime.
    
    Compilers will already have to implement the majority of these methods from the Compiler interface.
    """)
public interface ModifiableCompiler extends Cloneable {
    
    Class<?> load(byte[] bytecode, String name);
    
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
    
    default Map<Converter.Data, Converter<?, ?>> getConverters() {
        return new HashMap<>();
    }
    
    default Map<OperatorFunction.Data, OperatorFunction<?, ?>> getOperators() {
        return new HashMap<>();
    }
    
    default Promise<Resource[]> compileAsync(InputStream stream, Type name, Skript skript) {
        return this.background(() -> this.compile(stream, name), skript);
    }
    
    default Promise<Resource[]> background(Supplier<Resource[]> supplier, Skript skript) {
        return new Promise<>(CompletableFuture.supplyAsync(supplier, skript.getExecutor()));
    }
    
    Resource[] compile(InputStream stream, Type name);
    
    default Promise<Resource[]> compileAsync(InputStream file, String path, Skript skript) {
        return this.background(() -> compile(file, path), skript);
    }
    
    Resource[] compile(InputStream file, String path);
    
    default Promise<Resource[]> compileAsync(String file, Type path, Skript skript) {
        return this.background(() -> compile(file, path), skript);
    }
    
    Resource[] compile(String file, Type path);
    
    ModifiableCompiler clone();
    
}
