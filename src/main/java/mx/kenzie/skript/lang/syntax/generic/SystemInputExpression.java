package mx.kenzie.skript.lang.syntax.generic;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.skript.api.Instruction;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.CommonTypes;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.lang.element.StandardElements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class SystemInputExpression extends SimpleExpression {
    
    public SystemInputExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the ](system|console) input");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return true;
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith("input")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean requiresMainThread() {
        return true;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = SystemInputExpression.class.getMethod("get");
        this.writeCall(method, target, context);
    }
    
    public static String get() throws Throwable {
        final Instruction<String> instruction = new Instruction<>() {
            private String value;
            
            @Override
            public void run() throws Throwable {
                value = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            
            @Override
            public String get() {
                return value;
            }
        };
        SyntaxElement.runOnMainThread(instruction);
        return instruction.get();
    }
    
    public static String read() throws Throwable {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.OBJECT;
    }
    
}
