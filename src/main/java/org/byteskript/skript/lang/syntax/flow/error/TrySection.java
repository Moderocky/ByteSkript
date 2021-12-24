package org.byteskript.skript.lang.syntax.flow.error;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.TryCatchTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;

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
