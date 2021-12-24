package org.byteskript.skript.api;

public interface DataStore {
    
    <Thing> Thing retrieve(String identifier, Class<Thing> type);
    
    void store(String identifier, Object thing, Class<?> type);
    
    default void store(String identifier, Object thing) {
        if (thing == null) store(identifier, null, void.class);
        else store(identifier, thing, thing.getClass());
    }
    
}
