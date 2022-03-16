/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.threading;

import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.runtime.Skript;
import org.byteskript.skript.runtime.data.ScriptData;

import static org.byteskript.skript.runtime.internal.ConsoleColour.*;

/**
 * This produces nice, detailed error messages.
 */
public class ScriptExceptionHandler implements Thread.UncaughtExceptionHandler {
    
    @Override
    public void uncaughtException(Thread source, Throwable throwable) {
        if (throwable instanceof ThreadDeath) return;
        if (System.getProperty("debug_mode") != null)
            throwable.printStackTrace();
        if (throwable instanceof ScriptParseError error) { // these already look pretty.
            error.printStackTrace(System.err);
        } else if (source instanceof ScriptThread thread) {
            final Class<?> start = thread.initiator;
            System.err.println("An error occurred while running a script.");
            System.err.println("\t" + throwable.getMessage());
            if (start != null)
                System.err.println("This program started in: " + BLACK_BACKGROUND + YELLOW + start.getName()
                    .replace('.', '/') + ".bsk" + RESET);
            final StackTraceElement[] elements = throwable.getStackTrace();
            if (elements == null || elements.length < 1) return;
            System.err.println("The error came from:");
            if (elements[0].getClassName().startsWith("skript.")) {
                System.err.println("\t'" + RED + elements[0].getClassName() + RESET + "' line " + CYAN + elements[0].getLineNumber() + RESET);
                System.err.println("\t(This is from Skript code.)" + RESET);
            } else {
                System.err.println("\t'" + RED + elements[0].getClassName() + RESET + "' line " + CYAN + elements[0].getLineNumber() + RESET);
                System.err.println("\t(This is from a Java library.)" + RESET);
            }
            System.err.println("Below is the list of trigger calls that caused this error.");
            System.err.println("The top line was the most recent call.");
            for (final StackTraceElement element : elements) {
                final String location = element.getClassName();
                if (!location.startsWith("skript.")) continue;
                if (element.getLineNumber() < 1) continue;
                final StringBuilder error = new StringBuilder();
                final String method = element.getMethodName();
                if (method.startsWith("verify$")) {
                    error
                        .append("\tverifier ")
                        .append(PURPLE)
                        .append(method.substring(7))
                        .append(RESET);
                } else if (method.startsWith("event$")) {
                    error
                        .append("\tevent ")
                        .append(PURPLE)
                        .append(method.substring(6))
                        .append(RESET);
                } else {
                    error
                        .append("\tfunction ")
                        .append(PURPLE)
                        .append(method)
                        .append(RESET)
                        .append("(...)");
                }
                error
                    .append(" in '")
                    .append(BLACK_BACKGROUND)
                    .append(YELLOW)
                    .append(this.getScriptName(element))
                    .append(RESET)
                    .append("' at line ")
                    .append(CYAN)
                    .append(element.getLineNumber())
                    .append(RESET);
                System.err.println(error);
            }
        } else {
            System.err.print("Exception in thread \""
                + source.getName() + "\" ");
            throwable.printStackTrace(System.err);
        }
    }
    
    private String getScriptName(final StackTraceElement element) {
        final String location = element.getClassName();
        try {
            final Class<?> owner = Skript.localInstance().getClass(location);
            return owner.getAnnotation(ScriptData.class).sourceFile();
        } catch (Throwable ex) {
            return location.replace('.', '/') + ".bsk";
        }
    }
    
}
