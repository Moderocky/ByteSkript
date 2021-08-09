package mx.kenzie.skript.lang.syntax.event;

import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.syntax.TriggerHolder;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;

// Special event - we only call it for the current script
public class InitClassMember extends TriggerHolder {
    
    public InitClassMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "on load");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        if (context.getBuilder().getMatching(new MethodErasure(void.class, "<clinit>")) != null)
            throw new ScriptCompileError(context.lineNumber(), "Script already contains an initialiser event.");
    }
    
    @Override
    public Type returnType(Context context, Pattern.Match match) {
        return new Type(void.class);
    }
    
    @Override
    public Type[] parameters(Context context, Pattern.Match match) {
        return new Type[0];
    }
    
    @Override
    public String callSiteName(Context context, Pattern.Match match) {
        return "<clinit>";
    }
}
