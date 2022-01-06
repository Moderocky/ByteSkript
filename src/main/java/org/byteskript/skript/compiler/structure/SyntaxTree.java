/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.api.note.Effect;
import org.byteskript.skript.api.note.Expression;
import org.byteskript.skript.api.note.Property;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.lang.syntax.entry.syntax.ICreateSyntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final List<Handler> handlers;
    public HandlerType mode;
    
    public SyntaxTree(SectionMeta owner) {
        this.owner = owner;
        this.handlers = new ArrayList<>();
    }
    
    public List<Handler> getHandlers() {
        return handlers;
    }
    
    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
    }
    
    @Override
    public MultiLabel getEnd() {
        throw new IllegalStateException("This is not a programmatic tree.");
    }
    
    @Override
    public void start(Context context) {
    
    }
    
    @Override
    public void branch(Context context) {
    
    }
    
    @Override
    public void close(Context context) {
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return element instanceof ICreateSyntax;
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    public enum Type {
        EFFECT(Effect.class), EXPRESSION(Expression.class), PROPERTY(Property.class);
        public final Class<?> annotation;
        
        Type(Class<?> annotation) {
            this.annotation = annotation;
        }
    }
    
    public record Handler(Type type, String pattern) {
    }
}
