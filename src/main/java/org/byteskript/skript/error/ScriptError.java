/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface ScriptError {
    
    interface Output {
        void println(String string);
    }
    
    record OutputWriter(PrintStream stream, PrintWriter writer) implements Output {
        
        @Override
        public void println(String string) {
            if (stream != null) stream.println(string);
            else writer.println(string);
        }
        
    }
    
}
