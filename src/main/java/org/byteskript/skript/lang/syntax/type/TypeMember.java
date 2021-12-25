/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Member;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.data.SourceData;

import java.time.Instant;
import java.util.regex.Matcher;

public class TypeMember extends Member {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("type (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ")");
    
    public TypeMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "type");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 6) return null;
        if (!thing.startsWith("type ")) return null;
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
            .addAnnotation(org.byteskript.skript.runtime.data.Type.class).setVisible(true)
            .addValue("name", name)
            .addValue("template", false);
        builder
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
        builder.setModifiers(0x0001 | 0x0020);
        builder.addMethod("<init>")
            .writeCode(WriteInstruction.loadThis(), WriteInstruction.superObject(), WriteInstruction.returnEmpty());
        context.useSubBuilder(builder);
        context.addFlag(AreaFlag.IN_TYPE);
        context.setState(CompileState.ROOT); // members can go inside this!
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
