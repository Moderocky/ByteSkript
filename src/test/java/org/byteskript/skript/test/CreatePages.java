/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.autodoc.DocBuilder;

import java.io.File;
import java.io.IOException;

/**
 * This is not technically a test - but AutoDocs is available for testing.
 */
public class CreatePages {
    
    public static void main(String[] args) throws IOException {
        try (final DocBuilder builder = new DocBuilder("ByteSkript", new File("docs/"))) {
            builder.setJar(new File("target/ByteSkript.jar"));
            builder.setSourceRoot(new File("src/main/java"));
            builder.addClassesFrom("org.byteskript.skript.api").addClassesFrom("org.byteskript.skript.runtime");
            builder.setDescription("The API documentation for ByteSkript.");
            builder.setBody("""
                
                ## ByteSkript
                
                Visit the website [here](https://docs.byteskript.org).
                
                This website contains documentation for the [API](org/byteskript/skript/api/) and [Runtime](org/byteskript/skript/runtime/).
                """);
            builder.build();
        }
    }
    
}
