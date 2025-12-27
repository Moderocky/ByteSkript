package org.byteskript.skript.api.resource;

import mx.kenzie.foundation.language.PostCompileClass;

import java.io.*;
import java.nio.charset.Charset;
import java.util.jar.JarEntry;
import java.util.zip.ZipOutputStream;

/**
 * Represents a resource, such as a file, that may be copied to a ZIP archive.
 * */
public interface Resource {
    /**
     * Opens the source of this resource for reading.
     * @return A new input stream from which bytes are read when the resource is being copied.
     * @throws IOException If an I/O error occurs when opening the stream.
     * */
    InputStream open() throws IOException;

    /**
     * The name that the resource should have in the final ZIP archive.
     * @return A text with the name of the resource.
     * */
    String getEntryName();

    /**
     * Writes a resource to a ZIP output stream, closing any entries that were previously being written.
     * The resource's stream is opened, read, and closed.
     * @param outputStream The ZIP output stream to write to
     * @param resource The resource to copy
     * @throws IOException If an I/O error occurs when opening the stream or writing to the ZIP output stream.
     * */
    static void write(final ZipOutputStream outputStream, final Resource resource) throws IOException {
        try (final InputStream inputStream = resource.open()) {
            outputStream.putNextEntry(new JarEntry(resource.getEntryName()));
            inputStream.transferTo(outputStream);
            outputStream.closeEntry();
        }
    }

    /**
     * Creates a new resource containing some bytes. The resource will maintain a reference to the byte array, so
     * modifications to the byte array between calls to {@link Resource#write(ZipOutputStream, Resource)} will result
     * in different contents being written.
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param bytes The byte contents of the resource.
     * @return A resource containing the given bytes as its contents.
     * */
    static Resource ofBytes(final String name, final byte[] bytes) {
        return new Resource() {
            @Override
            public InputStream open() {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public String getEntryName() {
                return name;
            }
        };
    }

    /**
     * Creates a new resource with the given ZIP entry name, containing the given text encoded using the JVM default character set.
     * Equivalent to {@link Resource#ofString(String, String, Charset)} with {@link Charset#defaultCharset()}.
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param text The text that the resource should contain
     * @return The newly created resource.
     * */
    static Resource ofString(final String name, final String text) {
        return ofString(name, text, Charset.defaultCharset());
    }

    /**
     * Creates a new resource containing the given text encoded using the given character set.
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param text The text that the resource should contain
     * @param charset The character set that should be used to encode the text
     * @return The newly created resource.
     * */
    static Resource ofString(final String name, final String text, final Charset charset) {
        return ofBytes(name, text.getBytes(charset));
    }

    /**
     * Creates a new resource with the internal file name of the compiled class (<code>com/example/Main.class</code>) containing
     * the compiled code of the class.
     * @param compiledClass The compiled class to use as the source of the resource.
     * @return The newly created resource.
     * */
    static ClassResource ofCompiledClass(final PostCompileClass compiledClass) {
        return new ClassResource(compiledClass);
    }

    /**
     * Creates a new resource that will contain the contents of the given {@link File}. The file is accessed lazily, i.e.,
     * if the file is deleted before the resource is written to an archive, the resource will no longer be readable. To
     * load a file in memory and create a resource from its bytes, use {@link Resource#ofImmediateFile}
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param file The file that will act as the source for the contents of the resource
     * @return The newly created resource.
     * */
    static Resource ofFile(final String name, final File file) {
        return new Resource() {
            @Override
            public InputStream open() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public String getEntryName() {
                return name;
            }
        };
    }

    /**
     * Immediately reads all bytes in the given input stream and creates a new resource containing the read bytes.
     * This stores the entire stream contents in memory, so the stream may be closed and the resource
     * will remain readable. Equivalent to {@link Resource#ofBytes(String, byte[])} with {@link InputStream#readAllBytes()}.
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param stream The stream that will be read immediately.
     * @return The newly created resource with the contents of the stream.
     * */
    static Resource ofImmediate(final String name, final InputStream stream) throws IOException {
        return Resource.ofBytes(name, stream.readAllBytes());
    }

    /**
     * Immediately reads all bytes in the given file and creates a new resource containing the read bytes.
     * This stores the entire file in memory, so the file may be deleted and the resource will remain readable.
     * Equivalent to {@link Resource#ofImmediate(String, InputStream)} with {@link FileInputStream}.
     * @param name The name that the resource should have, were it to be added to a ZIP archive
     * @param file The file that will be read immediately.
     * @return The newly created resource with the contents of the file.
     * */
    static Resource ofImmediateFile(final String name, final File file) throws IOException {
        return Resource.ofImmediate(name, new FileInputStream(file));
    }
}
