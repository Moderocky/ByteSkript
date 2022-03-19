/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.control;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.ControlEffect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

@Documentation(
    name = "Add",
    description = """
        Add the first input to the given object.
        If the object is not a collection (or does not support adding) this will fail silently.
        This is not a mathematical operator expression.
        """,
    examples = {
        """
            add 1 to {list}
            """
    }
)
public class AddEffect extends ControlEffect {
    
    public AddEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "add %Object% to %Referent%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent();
        final ElementTree[] inputs = tree.nested();
        assert inputs.length == 2;
        if (!(inputs[1].current() instanceof final Referent referent))
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[1].current()
                .name() + "' cannot be added to.");
        final Method target = referent.getHandler(StandardHandlers.ADD);
        if (target == null)
            throw new ScriptParseError(context.lineNumber(), "Syntax '" + inputs[1].current()
                .name() + "' cannot be added to.");
        inputs[1].type = StandardHandlers.ADD;
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public HandlerType getType(Context context, Pattern.Match match) {
        return StandardHandlers.ADD;
    }
    
}
