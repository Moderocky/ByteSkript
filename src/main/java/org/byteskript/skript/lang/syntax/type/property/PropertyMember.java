/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type.property;

import mx.kenzie.foundation.FieldBuilder;
import org.byteskript.skript.api.syntax.Member;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptError;
import org.byteskript.skript.lang.element.StandardElements;

import java.util.regex.Matcher;

public class PropertyMember extends Member {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("property (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")");
    
    public PropertyMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "property");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("property ")) return null;
        if (context.hasFlag(AreaFlag.IN_PROPERTY)) return null;
        if (!context.hasFlag(AreaFlag.IN_TYPE)) {
            context.getError().addHint(this, "Properties can be declared only in types.");
            return null;
        }
        if (thing.length() < 12) {
            context.getError().addHint(this, "Property names must be at least 3 characters long.");
            return null;
        }
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find() && matcher.group("name") != null)
            return new Pattern.Match(matcher);
        context.getError()
            .addHint(this, "Property names must be " + ScriptError.ANSI_CYAN + "[a-z0-9_]" + ScriptError.ANSI_RESET + " and start with a letter.");
        return null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.removeFlag(AreaFlag.IN_PROPERTY);
        super.onSectionExit(context, meta);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = match.matcher().group("name");
        final FieldBuilder field = context.getBuilder().addField(name);
        context.setField(field);
        field.setModifiers(0x0001);
        field.setType(CommonTypes.OBJECT);
        context.addFlag(AreaFlag.IN_PROPERTY);
        context.setState(CompileState.MEMBER_BODY);
    }
    
}
