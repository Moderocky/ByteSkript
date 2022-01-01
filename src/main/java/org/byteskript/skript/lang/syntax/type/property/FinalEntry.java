/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type.property;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Final Property",
    description = """
        Makes this property final (unmodifiable.)
        Not currently supported.
        """
)
public class FinalEntry extends SimpleEntry {
    
    public FinalEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "final: %Boolean%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = (String) match.meta();
        final boolean value = Boolean.parseBoolean(name);
        if (value) context.getField().addModifiers(0x0010);
        else context.getField().removeModifiers(0x0010);
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("final: ")) return null;
        if (!thing.equals("final: true") && !thing.equals("final: false")) {
            context.getError().addHint(this, "A true/false value goes after the 'final:' entry.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), thing.substring(7));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return context.getState() == CompileState.MEMBER_BODY
            && context.hasFlag(AreaFlag.IN_TYPE)
            && context.hasFlag(AreaFlag.IN_PROPERTY);
    }
    
    
}
