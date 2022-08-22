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
    name = "Extend Type",
    description = "Specify a type this type can extend.",
    examples = {
        """
            type Square:
                extend: Shape
            """
    }
)
public class EntryExtends extends SimpleEntry {
    
    public EntryExtends() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "extend: %Type%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = match.meta();
        final Type type = context.findType(name);
        if (type != null) context.getBuilder().setSuperclass(type);
        else context.getBuilder().setSuperclass(new Type(name.replace('.', '/')));
        context.setState(CompileState.ROOT);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("extend: ")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        final String name = match.groups()[0].trim();
        if (name.isEmpty()) {
            context.getError().addHint(this, "A type should be specified after the 'extend:' entry.");
            return null;
        }
        if (name.contains("\"")) {
            context.getError().addHint(this, "Type names should not be written inside quotation marks.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), name);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return context.getState() == CompileState.ROOT && context.hasFlag(AreaFlag.IN_TYPE);
    }
    
    
}
