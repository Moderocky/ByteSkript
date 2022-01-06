/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;

import java.lang.reflect.Modifier;

public abstract class TriggerHolder extends Member {
    public TriggerHolder(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getBuilder()
            .addMethod(callSiteName(context, match))
            .addModifiers(Modifier.STATIC)
            .setReturnType(returnType(context, match))
            .addParameter(parameters(context, match));
        context.setMethod(method);
        context.setState(CompileState.MEMBER_BODY);
    }
    
    public abstract String callSiteName(Context context, Pattern.Match match);
    
    public abstract Type returnType(Context context, Pattern.Match match);
    
    public abstract Type[] parameters(Context context, Pattern.Match match);
    
}
