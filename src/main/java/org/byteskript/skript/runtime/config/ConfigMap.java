/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.config;

import mx.kenzie.jupiter.stream.InputStreamController;
import mx.kenzie.jupiter.stream.Stream;
import mx.kenzie.jupiter.stream.impl.StringBuilderOutputStream;
import org.byteskript.skript.error.ScriptRuntimeError;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An order-preserving map that represents entries in a `.csk` Skript config file.
 * This map is editable and can be saved to a target file.
 */
public class ConfigMap extends LinkedHashMap<String, ConfigEntry> {
    
    protected final File file;
    
    protected transient List<String> comments = new ArrayList<>();
    protected transient StringBuilder current = new StringBuilder();
    
    public ConfigMap() {
        this.file = null;
    }
    
    public ConfigMap(File file) throws IOException {
        this.file = file;
        if (file.exists() && file.canRead() && file.isFile()) {
            try (final InputStream stream = new FileInputStream(file)) {
                this.readLines(Stream.controller(stream));
            }
        }
    }
    
    protected void readLines(InputStreamController controller) {
        final AtomicBoolean comment = new AtomicBoolean(false);
        for (final String thing : controller.lines()) {
            final String line = this.stripLine(thing, comment);
            if (line.isEmpty()) continue;
            final int index = line.indexOf(':');
            if (index < 0) continue;
            final ConfigEntry entry = new ConfigEntry();
            entry.comments = comments.toArray(new String[0]);
            entry.key = line.substring(0, index).trim();
            entry.value = line.substring(index + 1).trim();
            this.put(entry);
            this.comments.clear();
        }
        
    }
    
    protected String stripLine(final String old, AtomicBoolean comment) {
        String line = old;
        boolean started = false;
        do {
            if (!comment.get()) {
                if (line.contains("//")) {
                    final String string = line.substring(line.indexOf("//") + 2).trim();
                    line = line.substring(0, line.indexOf("//")); // keep first part of line
                    this.comments.add(string);
                }
                if (line.contains("/*")) {
                    started = true;
                    comment.set(true);
                    final String string = line.substring(line.indexOf("/*") + 2);
                    line = line.substring(0, line.indexOf("/*")); // first part of line not in comment
                    this.current.append(string);
                }
            }
            if (comment.get()) {
                if (line.contains("*/")) {
                    if (!started) this.current.append(System.lineSeparator());
                    final String string = line.substring(0, line.indexOf("*/"));
                    line = line.substring(line.indexOf("*/") + 2); // keep last part of line
                    this.current.append(string);
                    this.comments.add(current.toString().trim());
                    this.current = new StringBuilder();
                    comment.set(false);
                } else {
                    if (!started) this.current.append(System.lineSeparator());
                    this.current.append(line);
                    line = ""; // inside a commented block
                }
            }
        } while (line.contains("/*") || line.contains("*/") || line.contains("//"));
        return line.trim(); // for now just trim lines, no indented areas
    }
    
    public ConfigEntry put(ConfigEntry value) {
        return super.put(value.key, value);
    }
    
    public ConfigMap(InputStream stream) throws IOException {
        this.file = null;
        try (final InputStreamController controller = Stream.controller(stream)) {
            this.readLines(controller);
        }
    }
    
    public static ConfigMap create(final InputStream stream) throws IOException {
        return new ConfigMap(stream);
    }
    
    public static ConfigMap create(final String path) throws IOException {
        return create(new File(path));
    }
    
    public static ConfigMap create(final File file) throws IOException {
        return new ConfigMap(file);
    }
    
    public static void add(String string, ConfigMap map) {
        final int index = string.indexOf(':');
        if (index < 0) return;
        final String key = string.substring(0, index).trim();
        final String value = string.substring(index + 1).trim();
        final ConfigEntry entry;
        if (map.containsKey(key)) entry = map.get(key);
        else entry = new ConfigEntry();
        map.putIfAbsent(key, entry);
        entry.key = key;
        entry.value = value;
    }
    
    public static Object getMapValue(Object key, Object target) {
        if (!(target instanceof ConfigMap map))
            throw new ScriptRuntimeError("The target must be a config.");
        return get(key + "", map);
    }
    
    public static String get(String key, ConfigMap map) {
        return map.getOrDefault(key, ConfigEntry.EMPTY).value;
    }
    
    public static void setMapValue(Object key, Object target, Object value) {
        if (!(target instanceof ConfigMap map))
            throw new ScriptRuntimeError("The target must be a config.");
        set(key + "", map, value != null ? value + "" : null);
    }
    
    public static void set(String key, ConfigMap map, String value) {
        final ConfigEntry entry;
        if (map.containsKey(key)) entry = map.get(key);
        else entry = new ConfigEntry();
        map.putIfAbsent(key, entry);
        assert map.containsKey(key);
        entry.key = key;
        entry.value = value; // update stored copies
        if (value == null) map.remove(key);
    }
    
    public static void deleteMapValue(Object key, Object target) {
        if (!(target instanceof ConfigMap map))
            throw new ScriptRuntimeError("The target must be a config.");
        set(key + "", map, null);
    }
    
    public void delete() {
        if (file == null) return;
        this.file.delete();
    }
    
    public void save() throws IOException {
        if (file == null) return;
        if (!file.exists()) file.createNewFile();
        try (final OutputStream stream = new FileOutputStream(file)) {
            this.write(stream);
        }
    }
    
    public void write(OutputStream stream) throws IOException {
        for (final ConfigEntry entry : this.values()) entry.write(stream);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        try (final StringBuilderOutputStream stream = new StringBuilderOutputStream(builder)) {
            for (final ConfigEntry entry : this.values()) entry.write(stream);
        } catch (IOException ignored) {}
        return builder.toString();
    }
    
}
