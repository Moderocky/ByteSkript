/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.syntax.Member;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.data.SourceData;
import org.byteskript.skript.runtime.data.TypeData;

import java.time.Instant;
import java.util.regex.Matcher;

public class TemplateTypeMember extends Member {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("template type (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")");
    
    public TemplateTypeMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "template type");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 6) return null;
        if (!thing.startsWith("template type ")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find() && matcher.group("name") != null)
            return new Pattern.Match(matcher);
        return null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.removeFlag(AreaFlag.IN_TYPE);
        context.endSubBuilder();
        super.onSectionExit(context, meta);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String name = match.matcher().group("name");
        final String path = context.getType().internalName() + "/" + name;
        final ClassBuilder builder = context.addSuppressedBuilder(Type.of(path));
        final Type type = builder.getType();
        context.registerType(name, type);
        builder
            .addAnnotation(TypeData.class).setVisible(true)
            .addValue("name", name)
            .addValue("template", true);
        builder
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("type", "template")
            .addValue("name", name)
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
        builder.setModifiers(0x0001 | 0x0400 | 0x0200);
        context.useSubBuilder(builder);
        context.addFlag(AreaFlag.IN_TYPE);
        context.addFlag(AreaFlag.IN_ABSTRACT_TYPE);
        context.setState(CompileState.ROOT); // members can go inside this!
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
