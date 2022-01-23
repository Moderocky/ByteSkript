/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Function Return Type",
    description = """
        Specify an explicit return type of a function.
        This can be used for overriding functions in types.
        """,
    examples = {
        """
            function my_func:
                return: String
                trigger:
                    return "hello"
                    """
    }
)
public class ReturnType extends SimpleEntry {
    
    public ReturnType() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "return: %Type%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = match.meta();
        final Type type = context.getType(name);
        if (type != null) context.getMethod().setReturnType(type);
        else context.getMethod().setReturnType(new Type(name.replace('.', '/')));
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("return: ")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        final String name = match.groups()[0].trim();
        if (name.contains("\"")) {
            context.getError().addHint(this, "Types should not be written inside quotation marks.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), name);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.hasFlag(AreaFlag.IN_FUNCTION);
    }
    
    
}
