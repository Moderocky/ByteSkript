/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface ScriptError {
    
    String BLACK_BACKGROUND = "\033[40m";
    String ANSI_RESET = "\u001B[0m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";
    
    
    record OutputWriter(PrintStream stream, PrintWriter writer) implements Output {
        
        @Override
        public void println(String string) {
            if (stream != null) stream.println(string);
            else writer.println(string);
        }
        
    }
    
    interface Output {
        void println(String string);
    }
    
}
