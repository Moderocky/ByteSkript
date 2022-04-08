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

import static org.byteskript.skript.runtime.internal.ConsoleColour.*;

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
    
    public ErrorDetails getDetails() {
        return details;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public void printStackTrace(PrintStream stream) {
        if (details == null) super.printStackTrace(stream);
        else printStackTrace(new OutputWriter(stream, null));
    }
    
    @Override
    public void printStackTrace(PrintWriter stream) {
        if (details == null) super.printStackTrace(stream);
        else printStackTrace(new OutputWriter(null, stream));
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        if (System.getProperty("debug_mode") != null)
            return super.fillInStackTrace();
        return this;
    }
    
    public void printStackTrace(OutputWriter stream) {
        stream.println(RESET + "A parsing error has occurred in:");
        stream.println("\tline " + CYAN + line + RESET + " of '" + BLACK_BACKGROUND + YELLOW + details.file + RESET + "'");
        if (details.expression != null) {
            stream.println(RESET + "No match was found for expression:\n\t'" + BLACK_BACKGROUND + YELLOW + details.expression + RESET + "'");
            stream.println(RESET + "In the line/effect:\n\t'" + BLACK_BACKGROUND + YELLOW + details.line + RESET + "'");
            stream.println(RESET + "This effect was matched to:\n\t'" + BLACK_BACKGROUND + YELLOW + details.lineMatched.name() + RESET + "'");
        } else if (details.line != null) {
            stream.println(RESET + "No match was found for line:\n\t'" + BLACK_BACKGROUND + YELLOW + details.line + RESET + "'");
        }
        if (details.hints.isEmpty()) return;
        stream.println(RESET + "Below are some hints from partial matches.");
        for (Map.Entry<SyntaxElement, String> entry : details.hints.entrySet()) {
            final SyntaxElement element = entry.getKey();
            final String hint = entry.getValue();
            stream.println(RESET + "If you were trying to use '" + BLACK_BACKGROUND + CYAN + element.name() + RESET + "':");
            stream.println(RESET + "\t" + hint);
        }
    }
    
}
