/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.resource.Resource;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.runtime.type.Converter;
import org.byteskript.skript.runtime.type.OperatorFunction;

import java.util.*;

@Description("""
    A library that provides syntax or runtime dependencies to the Skript implementation.
    
    Most implementations should use [ModifiableLibrary](ModifiableLibrary.html) instead
    - it is both safer and easier to use.
    
    The library tells the Skript runtime what syntax is available.
    """)
public interface Library {
    
    @Description("The library name.")
    String name();
    
    @Description("""
        Allows the library to filter syntax by the current context.
        
        This is designed to make matching faster, since the library knows how to
        exclude clearly-incorrect syntax from being checked for a match.
        
        The version in [ModifiableLibrary](ModifiableLibrary.html) is recommended,
        but libraries may implement a faster version.
        """)
    Collection<SyntaxElement> getHandlers(final State state, final LanguageElement expected, final Context context);
    
    @Description("""
        Return any property handlers this library provides.
        
        These are different from regular syntax, since they are
        compiled directly into the Property Handler expression.
        """)
    Collection<PropertyHandler> getProperties();
    
    @Description("Any custom language constructs provided by this library.")
    LanguageElement[] getConstructs();
    
    @Description("""
        Types provided by this library.
        
        These types can be written with their simple name in scripts,
        as can be done with `String` and `Number` by default,
        rather than needing the full `a/b/c/Type` path.
        """)
    Type[] getTypes();
    
    @Description("Runtime dependencies to be included in complete archive.")
    Collection<Resource> getRuntime();
    
    @Description("""
        Generates documentation for all available syntax to be exported to a processor.
        """)
    default Document[] generateDocumentation() {
        final List<Document> documents = new ArrayList<>();
        for (final SyntaxElement syntax : this.getSyntax()) {
            documents.add(syntax.createDocument());
        }
        return documents.toArray(new Document[0]);
    }
    
    @Description("""
        All syntax elements available in this library, without filtering.
        This is used by documentation scraping or special lookups to find the source of an error.
        """)
    SyntaxElement[] getSyntax();
    
    default Map<Converter.Data, Converter<?, ?>> getConverters() {
        return new HashMap<>();
    }
    
    default Map<OperatorFunction.Data, OperatorFunction<?, ?>> getOperators() {
        return new HashMap<>();
    }
    
}
