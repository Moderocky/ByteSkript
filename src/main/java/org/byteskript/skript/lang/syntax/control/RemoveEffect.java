/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.control;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class RemoveEffect extends ControlEffect {
    
    public RemoveEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "remove %Object% from %Referent%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 2;
        if (!(inputs[1].current() instanceof final Referent referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[1].current()
                .name() + "' cannot be removed from.");
        final Method target = referent.getHandler(StandardHandlers.REMOVE);
        if (target == null)
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be removed from.");
        inputs[1].compile = false;
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final ElementTree tree = context.getLine();
        final ElementTree[] inputs = tree.nested();
        final Referent referent = (Referent) inputs[1].current();
        final Method target = referent.getHandler(StandardHandlers.REMOVE);
        assert target != null;
        this.writeCall(method, target, context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.REMOVE;
    }
    
}