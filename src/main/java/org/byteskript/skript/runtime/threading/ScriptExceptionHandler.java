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
            final StringBuilder builder = new StringBuilder();
            final Class<?> start = thread.initiator;
            builder.append("An error occurred while running a script.\n");
            builder.append("\t").append(throwable.getClass().getSimpleName()).append(": ")
                .append(throwable.getMessage()).append("\n");
            if (start != null)
                builder.append("This program started in: " + BLACK_BACKGROUND + YELLOW).append(start.getName()
                    .replace('.', '/')).append(".bsk").append(RESET).append("\n");
            final StackTraceElement[] elements = throwable.getStackTrace();
            if (elements == null || elements.length < 1) return;
            builder.append("The error came from:" + "\n");
            if (elements[0].getClassName().startsWith("skript.")) {
                builder.append("\t'" + RED).append(elements[0].getClassName()).append(RESET).append("' line ")
                    .append(CYAN).append(elements[0].getLineNumber()).append(RESET).append("\n");
                builder.append("\t(This is from Skript code.)" + RESET + "\n");
            } else {
                builder.append("\t'" + RED + elements[0].getClassName() + RESET + "' line " + CYAN + elements[0].getLineNumber() + RESET + "\n");
                builder.append("\t(This is from a Java library.)" + RESET + "\n");
            }
            builder.append("Below is the list of trigger calls that caused this error.\n");
            builder.append("The top line was the most recent call.\n");
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
                builder.append(error);
            }
            synchronized (System.err) {
                System.err.println(builder);
            }
        } else synchronized (System.err) {
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
