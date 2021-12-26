/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.type;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Modifier;

public class TypeCreator extends SimpleExpression {
    
    public TypeCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a ]new %Type%");
        handlers.put(StandardHandlers.GET, findMethod(TypeCreator.class, "create", Object.class));
        handlers.put(StandardHandlers.FIND, findMethod(TypeCreator.class, "create", Object.class));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
    @ForceExtract
    public static Object create(Object object) {
        if (object == null) return null;
        if (!(object instanceof Class<?> type))
            throw new ScriptRuntimeError("Tried to create a new non-type thing: " + object);
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
            throw new ScriptRuntimeError("The type '" + ((Class<?>) object).getSimpleName() + "' is a template and cannot be created.");
        try {
            return type.newInstance();
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeError("The type '" + ((Class<?>) object).getSimpleName() + "' cannot be created like this.");
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeError("The type '" + ((Class<?>) object).getSimpleName() + "' does not permit creation.");
        }
    }
    
}
