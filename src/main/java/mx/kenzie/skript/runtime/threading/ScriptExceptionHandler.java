package mx.kenzie.skript.runtime.threading;

public class ScriptExceptionHandler implements Thread.UncaughtExceptionHandler {
    
    @Override
    public void uncaughtException(Thread source, Throwable throwable) {
        if (throwable instanceof ThreadDeath) return;
        if (source instanceof ScriptThread thread) {
            final Class<?> start = thread.initiator;
            
            System.err.println("An error has occurred.");
            if (start != null)
                System.err.println("Source: " + start.getName());
            throwable.printStackTrace(System.err);
            
        } else {
            System.err.print("Exception in thread \""
                + source.getName() + "\" ");
            throwable.printStackTrace(System.err);
        }
    }
}
