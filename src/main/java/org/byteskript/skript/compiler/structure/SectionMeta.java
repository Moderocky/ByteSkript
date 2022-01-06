/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import org.byteskript.skript.api.syntax.Section;

import java.util.ArrayList;
import java.util.List;

public final class SectionMeta {
    private final List<Section> handlers;
    private final List<Object> data;
    
    public SectionMeta(Section handler) {
        this.handlers = new ArrayList<>();
        this.handlers.add(handler);
        this.data = new ArrayList<>();
    }
    
    public List<Section> getHandlers() {
        return handlers;
    }
    
    public List<Object> getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return "SectionMeta[" +
            "handler=" + handler().name() + ']';
    }
    
    public Section handler() {
        return handlers.get(0);
    }
    
}
