/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Pattern;

import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class Element implements SyntaxElement {
    
    private final Pattern pattern;
    protected final Library provider;
    protected final LanguageElement type;
    protected final Handlers handlers = new Handlers();
    
    public Element(final Library provider, final LanguageElement type, final String... patterns) {
        this.pattern = new Pattern(Arrays.copyOf(patterns, patterns.length), provider);
        this.provider = provider;
        this.type = type;
    }
    
    public final Pattern getPattern() {
        return pattern;
    }
    
    @Override
    public String[] getPatterns() {
        return pattern.getPatterns();
    }
    
    @Override
    public Library getProvider() {
        return provider;
    }
    
    @Override
    public LanguageElement getType() {
        return type;
    }
    
    @Override
    public boolean hasHandler(HandlerType type) {
        return handlers.containsKey(type) && handlers.get(type) != null;
    }
    
    @Override
    public void setHandler(HandlerType type, Method method) {
        this.handlers.put(type, method);
    }
    
    @Override
    public Method getHandler(HandlerType type) {
        return handlers.get(type);
    }
    
}
