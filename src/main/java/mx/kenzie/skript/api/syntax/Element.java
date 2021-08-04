package mx.kenzie.skript.api.syntax;

import mx.kenzie.skript.api.HandlerType;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.Pattern;

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
    public String name() {
        return pattern.name();
    }
    
    @Override
    public boolean hasHandler(HandlerType type) {
        return handlers.containsKey(type) && handlers.get(type) != null;
    }
    
    @Override
    public Method getHandler(HandlerType type) {
        return handlers.get(type);
    }
    
}
