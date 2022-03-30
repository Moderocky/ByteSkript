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
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.syntax.literal.StringLiteral;

@Documentation(
    name = "Import Function",
    description = """
        Imports a function to allow referencing it as a local function in code.
        """,
    examples = {
        """
            dictionary:
                import function "my_func" from skript/otherscript
                
            function test:
                trigger:
                    run my_func() // short name
            """
    }
)
public class ImportFunctionEffect extends Effect {
    
    public ImportFunctionEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "(import|define) function %String% from %Type%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("function")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        if (!context.hasFlag(AreaFlag.IN_DICTIONARY)) {
            context.getError().addHint(this, "Functions must be imported in the dictionary.");
            return null;
        }
        return match;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final String name = new StringLiteral().parse(tree.nested()[0].match().meta());
        final Type type = tree.nested()[1].match().meta();
        context.registerFunction(new Function(name, type));
        context.setState(CompileState.CODE_BODY);
    }
}
