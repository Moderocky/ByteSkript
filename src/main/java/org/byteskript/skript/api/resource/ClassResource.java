package org.byteskript.skript.api.resource;

import mx.kenzie.foundation.language.PostCompileClass;

import java.io.*;

/**
 * A resource which represents a compiled class file.
 * @param source The source class file.
 * */
public record ClassResource(PostCompileClass source) implements Resource {
    @Override
    public InputStream open() {
        return new ByteArrayInputStream(source.code());
    }

    @Override
    public String getEntryName() {
        return source.internalName() + ".class";
    }
}
