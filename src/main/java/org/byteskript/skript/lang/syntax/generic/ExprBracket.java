/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.InnerModifyExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

@Documentation(
    name = "Brackets",
    description = """
        Used to help the parser determine which parts of a line are expressions.
        Bracketed content is always prioritised.
        """,
    examples = {
        """
            set {var} to 5 * (5 + 3)
                """
    }
)
public class ExprBracket extends InnerModifyExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\\((.+)\\)$");
    
    public ExprBracket() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "(brackets)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("(")) return null;
        if (!thing.endsWith(")")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (!matcher.find()) return null;
        return new Pattern.Match(matcher, CommonTypes.OBJECT);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }

    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }

}
