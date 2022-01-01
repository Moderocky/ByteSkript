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
    name = "Local Property",
    description = """
        Makes this property local (inaccessible.)
        Not currently supported.
        """
)
public class LocalEntry extends SimpleEntry {
    
    public LocalEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "local: %Boolean%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = (String) match.meta();
        final boolean value = Boolean.parseBoolean(name);
        if (value) {
            context.getField().removeModifiers(0x0001);
            context.getField().addModifiers(0x0002);
        } else {
            context.getField().removeModifiers(0x0002);
            context.getField().addModifiers(0x0001);
        }
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("local: ")) return null;
        if (!thing.equals("local: true") && !thing.equals("local: false")) {
            context.getError().addHint(this, "A true/false value goes after the 'local:' entry.");
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
