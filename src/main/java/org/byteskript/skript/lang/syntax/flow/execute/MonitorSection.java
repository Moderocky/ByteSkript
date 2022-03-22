/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.execute;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.MonitorTree;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Monitor Section",
    description = """
        Locks an object so only this process may use it during the block.
        Additionally waits for other locks on the same object to close before starting.
        """,
    examples = {
        """
            monitor {list}:
                add 10 to {list}
            """
    }
)
public class MonitorSection extends Section {
    
    public MonitorSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "monitor %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("monitor ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MonitorTree tree = new MonitorTree(context.getSection(1));
        this.compileTogether(context, tree);
    }
    
    public void compileTogether(Context context, MonitorTree tree) throws Throwable {
        context.createTree(tree);
        tree.start(context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public boolean isDelay() {
        return true;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof MonitorTree found) current = found;
        else current = context.getCurrentTree();
        if (current instanceof MonitorTree tree) tree.close(context);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        final MonitorTree tree = new MonitorTree(context.getSection());
        this.compileTogether(context, tree);
        tree.close(context);
    }
    
}
