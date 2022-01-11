/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.api.note.Effect;
import org.byteskript.skript.api.note.Expression;
import org.byteskript.skript.api.note.ForceBridge;
import org.byteskript.skript.api.note.Property;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Ignore
@Deprecated(forRemoval = true)
public class IOHandlers {
    
    @Property("reader")
    public static InputStream getInputStream(File file)
        throws Throwable {
        return new FileInputStream(file);
    }
    
    @Property("writer")
    public static OutputStream getOutputStream(File file)
        throws Throwable {
        return new FileOutputStream(file);
    }
    
    @Property("contents")
    public static String getContents(File file)
        throws Throwable {
        try (final InputStream stream = new FileInputStream(file)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    @Property("all")
    public static String read(InputStream stream)
        throws Throwable {
        return new String(stream.readAllBytes());
    }
    
    @Property("line")
    public static String readLine(InputStream stream)
        throws Throwable {
        return new BufferedReader(new InputStreamReader(stream)).readLine();
    }
    
    @ForceBridge
    @Effect("(write|append) %String% to %OutputStream%")
    public static void write(String contents, OutputStream stream)
        throws Throwable {
        stream.write(contents.getBytes(StandardCharsets.UTF_8));
    }
    
    @Property("name")
    public static String nameOf(File file) {
        return file.getName();
    }
    
    @Property("path")
    public static String pathOf(File file) {
        return file.getPath();
    }
    
    @Expression("%File% is [a ]folder")
    public static Boolean isFolder(File file) {
        return file.isDirectory();
    }
    
    @Expression("%File% is [a ]file")
    public static Boolean isFile(File file) {
        return file.isFile();
    }
    
    @Effect("clear %File%")
    public static void clear(File file)
        throws Throwable {
        setContents(file, "");
    }
    
    @Property(value = "contents", type = StandardHandlers.SET)
    public static void setContents(File file, String contents)
        throws Throwable {
        try (final OutputStream stream = new FileOutputStream(file)) {
            stream.write(contents.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    @Expression("[a ]new file at %String%")
    public static File create(String path)
        throws Throwable {
        final File file = new File(path);
        if (file.exists()) return file;
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();
        return file;
    }
    
    @Expression("[the ]file at %String%")
    public static File get(String path)
        throws Throwable {
        return new File(path);
    }
    
    @ForceBridge
    @Effect("delete file %File%")
    public static void delete(File file) {
        file.delete();
    }
    
    @ForceBridge
    @Effect({"close %InputStream%", "close %OutputStream%"})
    public static void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ignored) { // Likely already closed.
        }
    }
    
}
