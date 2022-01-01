/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

import java.lang.reflect.Method;

public abstract class ComplexExpression extends Element implements SyntaxElement {
    
    public ComplexExpression(final Library provider, final LanguageElement type, final String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        super.preCompile(context, match);
        final Method target = handlers.get(context.getHandlerMode());
        if (target == null) return;
        this.prepareExpectedTypes(context, target);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return state == CompileState.STATEMENT && context.hasCurrentUnit();
    }
    
}
