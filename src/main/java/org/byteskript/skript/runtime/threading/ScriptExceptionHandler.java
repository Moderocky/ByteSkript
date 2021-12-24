package org.byteskript.skript.runtime.threading;

public class ScriptExceptionHandler implements Thread.UncaughtExceptionHandler {
    
    @Override
    public void uncaughtException(Thread source, Throwable throwable) {
        if (throwable instanceof ThreadDeath) return;
        if (source instanceof ScriptThread thread) {
            final Class<?> start = thread.initiator;
            System.err.println("An error has occurred.");
            if (start != null)
                System.err.println("This program started in: " + start.getName().replace('.', '/'));
            final StackTraceElement[] elements = throwable.getStackTrace();
            if (elements == null || elements.length < 1) return;
            System.err.println("The error came from '" + elements[0].getClassName() + "' line " + elements[0].getLineNumber());
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
                        .append("  verifier ")
                        .append(method.substring(7));
                } else if (method.startsWith("event$")) {
                    error
                        .append("  event ")
                        .append(method.substring(6));
                } else {
                    error
                        .append("  function ")
                        .append(method)
                        .append("(...)");
                }
                error
                    .append(" in ")
                    .append(this.getScriptName(element))
                    .append(" at line ")
                    .append(element.getLineNumber());
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
        return location.replace('.', '/') + ".bsk";
    }
    
}
