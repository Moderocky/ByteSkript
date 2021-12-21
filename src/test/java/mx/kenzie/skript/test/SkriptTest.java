package mx.kenzie.skript.test;

import mx.kenzie.foundation.language.PostCompileClass;

import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class SkriptTest {
    
    protected static void debug(final PostCompileClass source) throws Throwable {
        try (final OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
