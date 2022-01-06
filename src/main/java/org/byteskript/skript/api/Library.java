/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.skript.compiler.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Library {
    
    String name();
    
    Collection<SyntaxElement> getHandlers(final State state, final LanguageElement expected, final Context context);
    
    Collection<PropertyHandler> getProperties();
    
    LanguageElement[] getConstructs();
    
    Type[] getTypes();
    
    /**
     * Runtime dependencies to be included in complete archives.
     */
    Collection<PostCompileClass> getRuntime();
    
    default Document[] generateDocumentation() {
        final List<Document> documents = new ArrayList<>();
        for (final SyntaxElement syntax : this.getSyntax()) {
            documents.add(syntax.createDocument());
        }
        return documents.toArray(new Document[0]);
    }
    
    SyntaxElement[] getSyntax();
    
}
