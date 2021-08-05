package mx.kenzie.skript.compiler;

import mx.kenzie.skript.api.syntax.Section;

import java.util.ArrayList;
import java.util.List;

public final class SectionMeta {
    private final List<Section> handlers;
    
    public SectionMeta(Section handler) {
        this.handlers = new ArrayList<>();
        this.handlers.add(handler);
    }
    
    public Section handler() {
        return handlers.get(0);
    }
    
    public List<Section> getHandlers() {
        return handlers;
    }
    
    @Override
    public String toString() {
        return "SectionMeta[" +
            "handler=" + handler().name() + ']';
    }
    
}
