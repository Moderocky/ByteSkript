/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry.syntax;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SyntaxTree;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Syntax Expression",
    description = """
        Registers the following pattern as an expression.
        Must be used inside the `syntax` block of a function.
        """,
    examples = {
        """
            function my_func:
                syntax:
                    expression: my [cool] expr
                trigger:
                    return "hello"
            """
    }
)
public class ExpressionEntry extends SimpleEntry {
    
    public ExpressionEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "expression: %Pattern%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String pattern = match.meta();
        final SyntaxTree tree = ((SyntaxTree) context.getCurrentTree());
        tree.addHandler(new SyntaxTree.Handler(SyntaxTree.Type.EXPRESSION, pattern));
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("expression: ")) return null;
        final String raw = thing.substring(12).trim();
        if (raw.isEmpty()) {
            context.getError().addHint(this, "A pattern needs to be written after the 'expression:' entry.");
            return null;
        }
        if (thing.contains("\"")) {
            context.getError().addHint(this, "Patterns should not contain quotation marks.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), raw);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.hasFlag(AreaFlag.IN_SYNTAX);
    }
    
    
}
