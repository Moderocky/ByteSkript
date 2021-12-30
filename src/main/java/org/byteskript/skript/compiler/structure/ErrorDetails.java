/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import org.byteskript.skript.api.SyntaxElement;

import java.util.HashMap;
import java.util.Map;

public class ErrorDetails {
    
    public String file;
    public String line;
    public SyntaxElement lineMatched;
    public String expression;
    public SyntaxElement expressionMatched;
    
    public final Map<SyntaxElement, String> hints = new HashMap<>();
    
    public void addHint(SyntaxElement source, String hint) {
        hints.put(source, hint);
    }
    
    @Override
    public ErrorDetails clone() {
        final ErrorDetails details = new ErrorDetails();
        details.file = file;
        details.line = line;
        details.lineMatched = lineMatched;
        details.expression = expression;
        details.expressionMatched = expressionMatched;
        details.hints.putAll(hints);
        return details;
    }
}
