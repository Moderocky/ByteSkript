/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import org.byteskript.skript.runtime.config.ConfigEntry;
import org.byteskript.skript.runtime.config.ConfigMap;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConfigTest extends SkriptTest {
    private final String test = """
        
        key: value
        
        my key: some long value :)
        
        // this is a comment!
        // this is another comment :o
        
        thing key: value
        
        /*
        this is a
        long, multi-line comment
        :) */
        blob: thing
        
        """;
    
    @Test
    public void testReadConfig() throws Throwable {
        final InputStream stream = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
        final ConfigMap map = ConfigMap.create(stream);
        assert map != null;
        assert map.size() == 4;
        assert map.containsKey("key");
        assert map.get("key").key.equals("key");
        assert map.get("key").value.equals("value");
        assert map.get("my key").value.equals("some long value :)");
        assert map.get("thing key").value.equals("value");
        assert map.get("blob").value.equals("thing");
        final ConfigEntry entry = map.get("thing key");
        assert entry.comments.length == 2;
        assert entry.comments[0].equals("this is a comment!");
        assert entry.comments[1].equals("this is another comment :o");
        assert map.get("blob").comments.length == 1;
        assert map.get("blob").comments[0].equals("this is a\n" + "long, multi-line comment\n" + ":)");
    }
    
    @Test
    public void testWriteConfig() {
        final ConfigMap map = new ConfigMap();
        map.put(new ConfigEntry("key", "value"));
        map.put(new ConfigEntry("hello", "there"));
        final ConfigEntry entry = new ConfigEntry("long key", "this is a long value :D");
        entry.comments = new String[] {"my comment!", "this is a\nlong comment!"};
        map.put(entry);
        assert map.toString().equals("""
            
            key: value
            hello: there
            // my comment!
            /*
            this is a
            long comment!
            */
            long key: this is a long value :D""");
    }
    
}
