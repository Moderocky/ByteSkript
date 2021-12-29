/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;

import java.io.PrintStream;

public class PrintEffect extends Effect {
    
    public PrintEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "print %Object%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.getField(System.class.getField("out")));
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.invokeVirtual(PrintStream.class.getMethod("println", Object.class)));
        context.setState(CompileState.CODE_BODY);
    }
    
}
