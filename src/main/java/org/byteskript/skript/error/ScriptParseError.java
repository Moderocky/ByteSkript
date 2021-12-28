/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.error;

import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.structure.ErrorDetails;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

public class ScriptParseError extends Error implements ScriptError {
    
    private final int line;
    private ErrorDetails details;
    
    public ScriptParseError(int line) {
        super();
        this.line = line;
    }
    
    public ScriptParseError(int line, String message) {
        super(message);
        this.line = line;
    }
    
    public ScriptParseError(int line, String message, Throwable cause) {
        super(message, cause);
        this.line = line;
    }
    
    public ScriptParseError(int line, ErrorDetails details, String message, Throwable cause) {
        super(message, cause);
        this.line = line;
        this.details = details;
    }
    
    public ScriptParseError(int line, Throwable cause) {
        super(cause);
        this.line = line;
    }
    
    protected ScriptParseError(int line, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.line = line;
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        if (System.getProperty("debug_mode") != null)
            return super.fillInStackTrace();
        return this;
    }
    
    @Override
    public void printStackTrace(PrintWriter stream) {
        if (details == null) super.printStackTrace(stream);
        printStackTrace(new OutputWriter(null, stream));
    }
    
    @Override
    public void printStackTrace(PrintStream stream) {
        if (details == null) super.printStackTrace(stream);
        printStackTrace(new OutputWriter(stream, null));
    }
    
    public void printStackTrace(OutputWriter stream) {
        stream.println(ANSI_RESET + "A parsing error has occurred in:");
        stream.println("\tline " + ANSI_CYAN + line + ANSI_RESET + " of '" + BLACK_BACKGROUND + ANSI_YELLOW + details.file + ANSI_RESET + "'");
        if (details.expression != null) {
            stream.println(ANSI_RESET + "No match was found for expression:\n\t'" + BLACK_BACKGROUND + ANSI_YELLOW + details.expression + ANSI_RESET + "'");
            stream.println(ANSI_RESET + "In the line/effect:\n\t'" + BLACK_BACKGROUND + ANSI_YELLOW + details.line + ANSI_RESET + "'");
            stream.println(ANSI_RESET + "This effect was matched to:\n\t'" + BLACK_BACKGROUND + ANSI_YELLOW + details.lineMatched.name() + ANSI_RESET + "'");
        } else if (details.line != null) {
            stream.println(ANSI_RESET + "No match was found for line:\n\t'" + BLACK_BACKGROUND + ANSI_YELLOW + details.line + ANSI_RESET + "'");
        }
        if (details.hints.isEmpty()) return;
        stream.println(ANSI_RESET + "Below are some hints from partial matches.");
        for (Map.Entry<SyntaxElement, String> entry : details.hints.entrySet()) {
            final SyntaxElement element = entry.getKey();
            final String hint = entry.getValue();
            stream.println(ANSI_RESET + "If you were trying to use '" + BLACK_BACKGROUND + ANSI_CYAN + element.name() + ANSI_RESET + "':");
            stream.println(ANSI_RESET + "\t" + hint);
        }
    }
    
}
