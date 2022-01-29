/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.timing;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.data.PeriodicalData;
import org.byteskript.skript.runtime.data.SourceData;

import java.lang.reflect.Modifier;
import java.time.Instant;

@Documentation(
    name = "Every",
    description = """
        Schedules a trigger to be run whenever a duration elapses.
        This will start after the script is loaded and the duration has elapsed for the first time.
        """,
    examples = {
        """
            every 5 seconds:
                trigger:
                    print "hello"
                """
    }
)
public class EveryMember extends TriggerHolder {
    public EveryMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "every %Duration%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("every ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public CompileState getSubState() {
        return CompileState.STATEMENT; // looking for expressions here
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final int index = context.lineNumber(); // we're going to fake the method here to get the duration at runtime
        final MethodBuilder method = context.getBuilder().addMethod("every$" + index + "_control")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .setReturnType(CommonTypes.DURATION);
        context.setMethod(method);
        context.addFlag(AreaFlag.IN_TRIGGER);
        context.setState(CompileState.STATEMENT);
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        context.removeFlag(AreaFlag.IN_TRIGGER);
        final int index = context.lineNumber();
        final MethodBuilder control = context.getMethod();
        control.writeCode(WriteInstruction.cast(CommonTypes.DURATION));
        control.writeCode(WriteInstruction.returnObject());
        final MethodBuilder method = context.getBuilder()
            .addMethod("every$" + index)
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .setReturnType(CommonTypes.VOID);
        context.setMethod(method);
        context.setState(CompileState.MEMBER_BODY);
        method
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("name", name())
            .addValue("type", "every")
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
        method
            .addAnnotation(PeriodicalData.class).setVisible(true)
            .addValue("control", "every$" + index + "_control")
            .addValue("fixed", true);
    }
    
    @Override
    public String callSiteName(Context context, Pattern.Match match) {
        return "every$" + context.lineNumber();
    }
    
    @Override
    public Type returnType(Context context, Pattern.Match match) {
        return CommonTypes.VOID;
    }
    
    @Override
    public Type[] parameters(Context context, Pattern.Match match) {
        return new Type[0];
    }
}
