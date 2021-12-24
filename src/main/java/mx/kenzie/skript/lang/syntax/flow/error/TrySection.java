package mx.kenzie.skript.lang.syntax.flow.error;

import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.MultiLabel;
import mx.kenzie.skript.compiler.structure.SectionMeta;
import mx.kenzie.skript.compiler.structure.TryCatchTree;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.lang.element.StandardElements;

public class TrySection extends Section {
    
    public TrySection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "try");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final TryCatchTree tree = new TryCatchTree(context.getSection(1), new MultiLabel());
        context.createTree(tree);
        tree.start(context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("try")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Try' must be used as section-header.");
    }
    
}
