/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.conditional;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.IfElseTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "Else",
    description = """
        Run if the preceding if-block fails.""",
    examples = {
        """
            if {var} is true:
                print "yes"
            else if {foo} is 6:
                print "maybe"
            else:
                print "no"
                    """
    }
)
public class ElseSection extends Section {
    
    public ElseSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "else");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("else")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        if (!(context.getTree(context.getSection(1)) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Else used without preceding if-section.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = tree.getNext();
        method.writeCode((writer, visitor) -> visitor.visitLabel(next));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        if (!(context.getTree(context.getSection()) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance if/else flow tree.");
        context.setState(CompileState.CODE_BODY);
        tree.close(context);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Else' must be used as section-header.");
    }
    
}
