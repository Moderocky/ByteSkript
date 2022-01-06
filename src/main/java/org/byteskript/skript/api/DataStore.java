/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

public interface DataStore {
    
    <Thing> Thing retrieve(String identifier, Class<Thing> type);
    
    default void store(String identifier, Object thing) {
        if (thing == null) store(identifier, null, void.class);
        else store(identifier, thing, thing.getClass());
    }
    
    void store(String identifier, Object thing, Class<?> type);
    
}
