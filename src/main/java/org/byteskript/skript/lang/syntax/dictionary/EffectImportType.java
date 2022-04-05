/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.dictionary;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Import Type",
    description = """
        Imports a type to allow referencing its short name in code.
        """,
    examples = {
        """
            dictionary:
                import type skript/otherscript/MyType
                
            function test:
                trigger:
                    set {thing} to a new MyType // short name
            """
    }
)
public class EffectImportType extends Effect {
    
    public EffectImportType() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(import|define) type %Type%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("type")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        if (!context.hasFlag(AreaFlag.IN_DICTIONARY)) {
            context.getError().addHint(this, "Types must be imported in the dictionary.");
            return null;
        }
        return match;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        tree.compile = false;
        final Type type = tree.match().meta();
        context.registerType(type);
        context.setState(CompileState.CODE_BODY);
    }
}
