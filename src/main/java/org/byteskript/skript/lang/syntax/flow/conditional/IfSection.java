/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.conditional;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.IfElseTree;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "If",
    description = """
        This block is run only if the boolean is true.""",
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
public class IfSection extends Section {
    
    public IfSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "if %Boolean%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("if ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final IfElseTree tree = new IfElseTree(context.getSection(1), new MultiLabel());
        context.createTree(tree);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = new Label();
        tree.setNext(next);
        method.writeCode(WriteInstruction.cast(CommonTypes.BOOLEAN));
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, next));
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
        final ProgrammaticSplitTree current = context.getCurrentTree();
        if (current instanceof IfElseTree tree) {
            context.setState(CompileState.CODE_BODY);
            final MethodBuilder method = context.getMethod();
            final Label label = tree.getNext();
            final Label end = tree.getEnd().use();
            method.writeCode((writer, visitor) -> {
                visitor.visitJumpInsn(167, end);
                visitor.visitLabel(label);
            });
        }
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label next = context.getCurrentTree().getNext();
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, next));
        context.setState(CompileState.CODE_BODY);
    }
    
}
