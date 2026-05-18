/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.automatic;

import org.byteskript.skript.api.note.Event;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.EventHolder;

/**
 * Represents an event holder generated automatically from a class annotated with {@link Event}.
 * @see Event
 * */
public final class GeneratedEventHolder extends EventHolder {
    
    private final Class<? extends org.byteskript.skript.api.Event> owner;
    
    public GeneratedEventHolder(Library provider, Class<? extends org.byteskript.skript.api.Event> owner, String... patterns) {
        super(provider, patterns);
        this.owner = owner;
    }
    
    @Override
    public Class<? extends org.byteskript.skript.api.Event> eventClass() {
        return owner;
    }
}
