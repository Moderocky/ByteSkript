/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class ConfigEntry {
    
    public static final ConfigEntry EMPTY = new ConfigEntry();
    
    public volatile String key;
    public volatile String value;
    public volatile String[] comments;
    
    public ConfigEntry(String key, String value) {
        this();
        this.key = key;
        this.value = value;
    }
    
    public ConfigEntry() {
        this.comments = new String[0];
    }
    
    public synchronized String key() {
        return key;
    }
    
    public synchronized String value() {
        return value;
    }
    
    public synchronized String[] comments() {
        return comments;
    }
    
    public synchronized void set(String value) {
        this.value = value;
    }
    
    public synchronized void addComment(String comment) {
        this.comments = Arrays.copyOf(comments, comments.length + 1);
        this.comments[comments.length - 1] = comment;
    }
    
    public synchronized void write(OutputStream stream) throws IOException {
        if (key == null || value == null) return;
        final byte[] newline = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
        for (final String comment : comments) {
            stream.write(newline);
            if (comment.contains(System.lineSeparator()) || comment.contains("\n")) {
                stream.write("/*".getBytes(StandardCharsets.UTF_8));
                stream.write(newline);
                for (final String line : comment.lines().toList()) {
                    stream.write(line.getBytes(StandardCharsets.UTF_8));
                    stream.write(newline);
                }
                stream.write("*/".getBytes(StandardCharsets.UTF_8));
            } else {
                stream.write("// ".getBytes(StandardCharsets.UTF_8));
                stream.write(comment.trim().getBytes(StandardCharsets.UTF_8));
            }
        }
        stream.write(newline);
        stream.write(key.trim().getBytes(StandardCharsets.UTF_8));
        stream.write(": ".getBytes(StandardCharsets.UTF_8));
        stream.write(value.trim().getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(key, value);
        result = 31 * result + Arrays.hashCode(comments);
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigEntry entry)) return false;
        return Objects.equals(key, entry.key) && Objects.equals(value, entry.value) && Arrays.equals(comments, entry.comments);
    }
    
    @Override
    public String toString() {
        return key + ": " + value;
    }
}
