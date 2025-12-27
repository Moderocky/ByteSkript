/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.Compiler;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.resource.Resource;
import org.byteskript.skript.runtime.internal.ModifiableCompiler;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.OperatorFunction;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class SkriptCompiler implements Compiler<SkriptLangSpec>, ModifiableCompiler {
    
    public static SkriptCompiler createBasic() {
        return new SimpleSkriptCompiler();
    }
    
    @Override
    public SkriptLangSpec getLanguage() {
        return SkriptLangSpec.INSTANCE;
    }
    
    public abstract Class<?> load(byte[] bytecode, String name);
    
    public abstract boolean addLibrary(Library library);
    
    public abstract boolean removeLibrary(Library library);
    
    @Override
    public Map<Converter.Data, Converter<?, ?>> getConverters() {
        final Map<Converter.Data, Converter<?, ?>> map = new HashMap<>();
        for (final Library library : this.getLibraries()) {
            map.putAll(library.getConverters());
        }
        return map;
    }
    
    @Override
    public Map<OperatorFunction.Data, OperatorFunction<?, ?>> getOperators() {
        final Map<OperatorFunction.Data, OperatorFunction<?, ?>> map = new HashMap<>();
        for (final Library library : this.getLibraries()) {
            map.putAll(library.getOperators());
        }
        return map;
    }
    
    public abstract Resource[] compile(InputStream stream, Type name);
    
    public abstract Resource[] compile(InputStream file, String path);
    
    public abstract Resource[] compile(String file, Type path);
    
    @Override
    public SkriptCompiler clone() {
        return this; // non-cloneable implementation
    }
}
