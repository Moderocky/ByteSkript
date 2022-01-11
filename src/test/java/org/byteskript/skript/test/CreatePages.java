/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import mx.kenzie.autodoc.AutoDocs;

import java.io.File;
import java.io.IOException;

/**
 * This is not technically a test - but AutoDocs is available for testing.
 */
public class CreatePages {
    
    public static void main(String[] args) throws IOException {
        AutoDocs.generateDocumentation(
            "ByteSkript",
            "The API documentation for ByteSkript.",
            """
                
                ## ByteSkript
                
                Visit the website [here](https://docs.byteskript.org).
                
                This website contains documentation for the [API](org/byteskript/skript/api/) and [Runtime](org/byteskript/skript/runtime/).
                """,
            new File("docs/"),
            new File("target/ByteSkript.jar"),
            "org.byteskript.skript.api",
            "org.byteskript.skript.runtime");
        
    }
    
}
