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
    name = "Use Template",
    description = "Specify a template this type uses.",
    examples = {
        """
            type Square:
                template: Shape
            """
    }
)
public class Template extends SimpleEntry {
    
    public Template() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "template: %Type%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = (String) match.meta();
        final Type type = context.getType(name);
        if (type != null) context.getBuilder().addInterfaces(type);
        else context.getBuilder().addInterfaces(new Type(name.replace('.', '/')));
        context.setState(CompileState.ROOT);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("template: ")) return null;
        final Pattern.Match match = super.match(thing, context);
        if (match == null) return null;
        final String name = match.groups()[0].trim();
        if (name.isEmpty()) {
            context.getError().addHint(this, "A type should be specified after the 'template:' entry.");
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
    public boolean allowedIn(State state, Context context) {
        return context.getState() == CompileState.ROOT && context.hasFlag(AreaFlag.IN_TYPE);
    }
    
    
}
