/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Function Parameter Types",
    description = """
        Specify explicit parameter types of a function.
        This can be used for overriding functions in types.
        This can also be used for automatic type conformity.
        """,
    examples = {
        """
            function my_func (a, b):
                parameters: string, number
                return: String
                trigger:
                    return "hello " + {a}
                    """
    }
)
public class Parameters extends SimpleEntry {
    
    public Parameters() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "parameters: %Types%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String[] names = match.<String>meta().split(",");
        final MethodBuilder builder = context.getMethod();
        final Type[] types = builder.getErasure().parameterTypes();
        for (int i = 0; i < names.length && i < types.length; i++) {
            final String name = names[i].trim();
            if (name.isEmpty()) {
                context.getError().addHint(this, "All values need to be provided in a `x, y, z` comma-separated list.");
                throw new ScriptCompileError(context.lineNumber(), "All values in " + match.meta() + " need to be provided in a `x, y, z` comma-separated list.");
            } else {
                final Type type = context.findType(name);
                if (type != null) builder.setParameter(i, type);
                else builder.setParameter(i, new Type(name.replace('.', '/')));
            }
        }
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("parameters: ")) return null;
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
