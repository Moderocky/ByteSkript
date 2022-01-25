/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.runtime.internal.ModifiableCompiler;

import java.io.InputStream;

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
    
    public abstract PostCompileClass[] compile(InputStream stream, Type name);
    
    public abstract PostCompileClass[] compile(InputStream file, String path);
    
    public abstract PostCompileClass[] compile(String file, Type path);
    
    @Override
    public SkriptCompiler clone() {
        return this; // non-cloneable implementation
    }
}
