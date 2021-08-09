package mx.kenzie.skript.api.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.AsyncEvent;
import mx.kenzie.skript.api.Event;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.runtime.data.EventData;
import mx.kenzie.skript.runtime.data.SourceData;

import java.lang.reflect.Modifier;
import java.time.Instant;

public abstract class EventHolder extends TriggerHolder {
    public EventHolder(Library provider, String... patterns) {
        super(provider, StandardElements.MEMBER, patterns);
    }
    
    public abstract Class<? extends Event> eventClass();
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final Class<? extends Event> eventClass = eventClass();
        final MethodBuilder method = context.getBuilder()
            .addMethod("event$" + eventClass.getSimpleName() + "_" + context.methodIndexShift())
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .setReturnType(CommonTypes.VOID);
        context.setMethod(method);
        context.setState(CompileState.MEMBER_BODY);
        method
            .addAnnotation(SourceData.class).setVisible(true)
            .addValue("line", context.lineNumber())
            .addValue("compiled", Instant.now().getEpochSecond());
        method
            .addAnnotation(EventData.class).setVisible(true)
            .addValue("name", name())
            .addValue("event", org.objectweb.asm.Type.getObjectType(new Type(eventClass).internalName()))
            .addValue("async", AsyncEvent.class.isAssignableFrom(eventClass));
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
        return null;
    }
}
