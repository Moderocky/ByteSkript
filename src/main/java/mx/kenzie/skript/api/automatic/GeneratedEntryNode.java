package mx.kenzie.skript.api.automatic;

import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.Library;
import mx.kenzie.skript.api.syntax.Element;
import mx.kenzie.skript.api.syntax.Literal;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.ElementTree;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.error.ScriptParseError;
import mx.kenzie.skript.lang.element.StandardElements;

import java.lang.reflect.RecordComponent;

public class GeneratedEntryNode extends Element {
    
    private final RecordComponent target;
    
    public GeneratedEntryNode(Library provider, final RecordComponent target, String... patterns) {
        super(provider, StandardElements.NODE, patterns);
        this.target = target;
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection().handler() instanceof GeneratedEntrySection section
            && section.getTarget() == target.getDeclaringRecord();
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        if (!(tree.current() instanceof Literal))
            throw new ScriptParseError(context.lineNumber(), "Entry node must contain literal value.");
        tree.compile = false;
        context.setState(CompileState.STATEMENT);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree tree = context.getCompileCurrent().nested()[0];
        if (!(tree.current() instanceof Literal<?> literal))
            throw new ScriptParseError(context.lineNumber(), "Entry node must contain literal value.");
        final Object object = literal.parse(tree.match().matcher().group());
        assert object == null || target.getType().isInstance(object);
        context.getSection().getData().add(object);
        context.setState(CompileState.AREA_BODY);
    }
    
}
