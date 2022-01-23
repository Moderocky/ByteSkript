/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.lambda;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.ExtractedSection;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

/**
 * A dangerous instruction form.
 * To be used internally by the new bake-phase to inline calls.
 */
@Documentation(
    name = "Lemma",
    description = """
        Creates a section of re-usable code.
        """,
    examples = {}
)
public class LemmaSection extends ExtractedSection {
    
    public LemmaSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new lemma");
    }
    
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (true) return null; // todo: not implemented yet.
        if (!thing.contains(" new lemma")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.EXECUTABLE.equals(type);
    }
    
    
}
