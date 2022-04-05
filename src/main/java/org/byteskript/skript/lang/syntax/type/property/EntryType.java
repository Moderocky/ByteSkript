/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type.property;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Property Type",
    description = """
        The type of this property.
        This can be used to allow only certain values.
        """,
    examples = """
        type Square:
            property sides:
                type: Integer
        """
)
public class EntryType extends SimpleEntry {
    
    public EntryType() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "type: %Type%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = match.meta();
        final Type type = context.findType(name);
        if (type != null) context.getField().setType(type);
        else context.getField().setType(new Type(name.replace('.', '/')));
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("type: ")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        final String name = match.groups()[0].trim();
        if (name.isEmpty()) {
            context.getError().addHint(this, "A type should be specified after the 'type:' entry.");
            return null;
        }
        if (name.contains("\"")) {
            context.getError().addHint(this, "Type names should not be written inside quotation marks.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), name);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return context.getState() == CompileState.MEMBER_BODY
            && context.hasFlag(AreaFlag.IN_TYPE)
            && context.hasFlag(AreaFlag.IN_PROPERTY);
    }
    
    
}
