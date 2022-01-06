/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;

import java.util.regex.Matcher;

@Documentation(
    name = "Function (No Arguments)",
    description = """
        A function with no arguments.
        This does not need the `()` brackets.
        """,
    examples = {
        """
            function my_func:
                trigger:
                    print "hello!"
                """
    }
)
public class NoArgsFunctionMember extends FunctionMember {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("function (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")(?:\\(\\))?");
    
    public NoArgsFunctionMember() {
        super();
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 10) return null;
        if (!thing.startsWith("function ")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find() && matcher.group("name") != null)
            return new Pattern.Match(matcher);
        return null;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public String callSiteName(Context context, Pattern.Match match) {
        return match.matcher().group("name");
    }
    
    @Override
    public Type returnType(Context context, Pattern.Match match) {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Type[] parameters(Context context, Pattern.Match match) {
        return new Type[0];
    }
}
