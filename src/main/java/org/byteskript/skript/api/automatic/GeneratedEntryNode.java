/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.automatic;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.Element;
import org.byteskript.skript.api.syntax.Literal;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.RecordComponent;

public class GeneratedEntryNode extends Element {
    
    private final RecordComponent target;
    
    public GeneratedEntryNode(Library provider, final RecordComponent target, String... patterns) {
        super(provider, StandardElements.NODE, patterns);
        this.target = target;
    }

    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }

    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        if (!(tree.current() instanceof Literal))
            throw new ScriptParseError(context.lineNumber(), "Entry node must contain literal value.");
        tree.compile = false;
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        if (!(tree.current() instanceof Literal<?> literal))
            throw new ScriptParseError(context.lineNumber(), "Entry node must contain literal value.");
        final Object object = literal.parse(tree.match().matcher().group());
        assert object == null || target.getType().isInstance(object);
        context.getSection().getData().add(object);
        context.setState(CompileState.AREA_BODY);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection().handler() instanceof GeneratedEntrySection section
            && section.getTarget() == target.getDeclaringRecord();
    }
    
}
