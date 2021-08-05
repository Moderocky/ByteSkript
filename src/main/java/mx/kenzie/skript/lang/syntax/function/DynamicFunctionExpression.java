package mx.kenzie.skript.lang.syntax.function;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.syntax.SimpleExpression;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.syntax.literal.StringLiteral;
import mx.kenzie.skript.runtime.Bootstrapper;
import org.objectweb.asm.Handle;

public class DynamicFunctionExpression extends SimpleExpression {
    
    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(SkriptLangSpec.IDENTIFIER.pattern() + "\\(\\)");
    
    public DynamicFunctionExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION,
            "[the ]function %String%",
            "[the ]function %String% with %Number% arg[ument]s");
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.EXECUTABLE;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree element = context.getCompileCurrent().nested()[0];
        if (!(element.current() instanceof StringLiteral))
            throw new ScriptCompileError(context.lineNumber(), "Dynamic function calls may accept only String literals.");
        element.compile = false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        assert match.meta() instanceof Integer;
        if ((int) match.meta() == 1) compileNoArgs(context, match);
        else compileWithArgs(context, match);
    }
    
    private void compileNoArgs(Context context, Pattern.Match match) {
        final Handle bootstrap = Bootstrapper.getBootstrap(false, true);
        final String name = match.groups()[0];
        final WriteInstruction instruction = WriteInstruction.invokeDynamic(CommonTypes.OBJECT, name.substring(1, name.length() - 1), new Type[0], bootstrap, org.objectweb.asm.Type.getType(context.getType()
            .descriptor()));
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(instruction);
    }
    
    private void compileWithArgs(Context context, Pattern.Match match) throws Throwable {
        throw new RuntimeException("didn't do this yet sorry :(");
    }
    
}
