/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.automatic.*;
import org.byteskript.skript.api.note.Property;
import org.byteskript.skript.api.note.*;
import org.byteskript.skript.api.syntax.EventHolder;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

public interface SyntaxAnnotationUnwrapper extends Library {
    
    default void generateSyntaxFrom(Class<?> owner) {
        type:
        {
            final SkriptType type = owner.getAnnotation(SkriptType.class);
            if (type == null) break type;
            this.registerType(owner);
        }
        event:
        {
            if (!Event.class.isAssignableFrom(owner)) break event;
            final org.byteskript.skript.api.note.Event event = owner.getAnnotation(org.byteskript.skript.api.note.Event.class);
            if (event == null) break event;
            final GeneratedEventHolder syntax = new GeneratedEventHolder(this, (Class<? extends Event>) owner, event.value());
            this.registerEvent(syntax);
        }
        entry_section:
        {
            if (!owner.isRecord()) break entry_section;
            final EntrySection section = owner.getAnnotation(EntrySection.class);
            if (section == null) break entry_section;
            final GeneratedEntrySection box = new GeneratedEntrySection(this, (Class<Record>) owner, section.value());
            this.registerSyntax(CompileState.MEMBER_BODY, box);
            for (RecordComponent component : owner.getRecordComponents()) {
                final EntryNode node = component.getAnnotation(EntryNode.class);
                if (node == null) continue;
                final GeneratedEntryNode thing = new GeneratedEntryNode(this, component, node.value());
                this.registerSyntax(CompileState.AREA_BODY, thing);
            }
        }
        for (Method method : owner.getDeclaredMethods()) {
            effect:
            {
                final Effect effect = method.getAnnotation(Effect.class);
                if (effect == null) break effect;
                final GeneratedEffect syntax = new GeneratedEffect(this, method, effect.value());
                this.registerSyntax(CompileState.CODE_BODY, syntax);
            }
            expression:
            {
                final Expression expression = method.getAnnotation(Expression.class);
                if (expression == null) break expression;
                final GeneratedExpression syntax = new GeneratedExpression(this, method, expression.value());
                this.registerSyntax(CompileState.STATEMENT, syntax);
            }
            property:
            {
                final org.byteskript.skript.api.note.Property property = method.getAnnotation(Property.class);
                if (property == null) break property;
                this.registerProperty(property.value(), property.type(), method);
            }
        }
    }
    
    Type registerType(Class<?> cls);
    
    void registerEvent(EventHolder event);
    
    void registerSyntax(State state, SyntaxElement element);
    
    void registerProperty(String name, HandlerType type, Method handler);
    
    default void registerValues(EventHolder event) {
        for (Method method : event.eventClass().getMethods()) {
            final EventValue value = method.getAnnotation(EventValue.class);
            if (value == null) continue;
            if (method.getParameterTypes().length == 0 && method.getReturnType() != void.class) {
                this.registerProperty(value.value(), StandardHandlers.GET, method);
            } else if (method.getParameterTypes().length == 1 && method.getReturnType() == void.class) {
                this.registerProperty(value.value(), StandardHandlers.SET, method);
            } else {
                throw new ScriptCompileError(-1, "Unable to extract event value handler from '" + method + "'");
            }
        }
    }
}
