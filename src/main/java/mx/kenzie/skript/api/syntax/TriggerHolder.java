package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.LanguageElement;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.runtime.data.SourceData;

import java.lang.reflect.Modifier;
import java.time.Instant;

public abstract class TriggerHolder extends Member {
    public TriggerHolder(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    public abstract Type returnType(Context context, Pattern.Match match);
    
    public abstract Type[] parameters(Context context, Pattern.Match match);
    
    public abstract String callSiteName(Context context, Pattern.Match match);
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder method = context.getBuilder()
            .addMethod(callSiteName(context, match))
            .addModifiers(Modifier.STATIC)
            .setReturnType(returnType(context, match))
            .addParameter(parameters(context, match));
        context.setMethod(method);
        context.setState(CompileState.MEMBER_BODY);
        method
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
    }
    
}
