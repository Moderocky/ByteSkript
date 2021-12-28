/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.control;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.Arrays;

public class SetEffect extends ControlEffect {
    
    public SetEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "set %Referent% to %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("set ") || !thing.contains(" to ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 2;
        if (!(inputs[0].current() instanceof Referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[0].current()
                .name() + "' cannot be set.");
        inputs[0].type = StandardHandlers.SET;
        final ElementTree[] trees = inputs[0].falseCopy();
        final ElementTree[] replacement = Arrays.copyOf(inputs[0].nested(), trees.length + 2);
        replacement[replacement.length - 2] = inputs[1];
        replacement[replacement.length - 1] = inputs[0];
        tree.replaceNest(replacement);
        inputs[0].replaceNest(trees);
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.SET;
    }
    
}
