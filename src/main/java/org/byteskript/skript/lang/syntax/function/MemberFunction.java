/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.Function;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.data.SourceData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Documentation(
    name = "Function",
    description = """
        A function that can be called from any code.
        Function names are alphanumeric and allow underscores.
        """,
    examples = {
        """
            function my_func (name, age):
                trigger:
                    print "my name is " + {name}
                    print "I am " + {age}
                """
    }
)
public class MemberFunction extends TriggerHolder {
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("function (?<name>" + SkriptLangSpec.IDENTIFIER.pattern() + ") ?\\((?<params>@?" + SkriptLangSpec.IDENTIFIER.pattern() + "(?:, @?" + SkriptLangSpec.IDENTIFIER.pattern() + ")*)\\)");
    
    public MemberFunction() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "function(...)");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (thing.length() < 10) return null;
        if (!thing.startsWith("function ")) return null;
        if (!thing.contains("(") || !thing.contains(")")) return null;
        final Matcher matcher = PATTERN.matcher(thing);
        if (matcher.find() && matcher.group("name") != null && matcher.group("params") != null)
            return new Pattern.Match(matcher);
        return null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.removeFlag(AreaFlag.IN_FUNCTION);
        if (!context.hasFlag(AreaFlag.IN_TYPE))
            context.registerFunction(new Function(context.getType(), context.getMethod().getErasure()));
        super.onSectionExit(context, meta);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        super.compile(context, match);
        final MethodBuilder method = context.getMethod();
        context.addFlag(AreaFlag.IN_FUNCTION);
        if (context.hasFlag(AreaFlag.IN_TYPE)) {
            method.setModifiers(0x0001);
            final PreVariable variable = new PreVariable("<this>");
            variable.internal = true;
            ((FileContext) context).getVariables().add(0, variable);
        }
        if (context.hasFlag(AreaFlag.IN_ABSTRACT_TYPE))
            context.getMethod().addModifiers(0x0400); // trigger will default this
        method
            .addAnnotation(org.byteskript.skript.runtime.data.Function.class).setVisible(true)
            .addValue("name", method.getErasure().name())
            .addValue("arguments", method.getErasure().parameterTypes().length)
            .addValue("async", false);
        
        method
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("name", name())
            .addValue("type", "function")
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
    }
    
    @Override
    public String callSiteName(Context context, Pattern.Match match) {
        return match.matcher().group("name");
    }
    
    @Override
    public Type returnType(Context context, Pattern.Match match) {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public Type[] parameters(Context context, Pattern.Match match) {
        final String string = match.matcher().group("params");
        final String[] strings = string.split(",");
        final List<Type> types = new ArrayList<>();
        for (String s : strings) {
            final String name = s.trim();
            if (name.isEmpty()) throw new ScriptParseError(context.lineNumber(), "Empty function parameter.");
            final PreVariable variable = context.getVariable(name);
            variable.parameter = true;
            if (name.charAt(0) == '@') {
                variable.atomic = true;
                types.add(CommonTypes.ATOMIC);
            } else types.add(CommonTypes.OBJECT);
        }
        return types.toArray(new Type[0]);
    }
}
