/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;

@Ignore
public enum ConsoleColour {
    RESET("\033[0m"),
    BLACK("\033[0;30m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    PURPLE("\033[0;35m"),
    CYAN("\033[0;36m"),
    CYAN_UNDERLINED("\033[4;36m"),
    BRIGHT_PURPLE("\033[0;95m"),
    BLACK_BACKGROUND("\033[40m");
    
    private final String code;
    
    ConsoleColour(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        if (System.console() == null || System.getenv().get("TERM") != null) {
            return code;
        } else {
            return "";
        }
    }
}
