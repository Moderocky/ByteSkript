/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.conditional;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.IfElseTree;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "Else If",
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
public class ElseIfSection extends Section {
    
    public ElseIfSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "else if %Boolean%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("else if ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Else-if used without preceding if-section.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = new Label();
        tree.setNext(next);
        method.writeCode(WriteInstruction.cast(CommonTypes.BOOLEAN));
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, next));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof IfElseTree found) current = found;
        else current = context.getCurrentTree();
        if (!(current instanceof IfElseTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance if/else flow tree.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        final Label label = tree.getNext();
        final Label end = tree.getEnd().use();
        method.writeCode((writer, visitor) -> {
            visitor.visitJumpInsn(167, end);
            visitor.visitLabel(label);
        });
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Else-if' must be used as section-header.");
    }
    
}
