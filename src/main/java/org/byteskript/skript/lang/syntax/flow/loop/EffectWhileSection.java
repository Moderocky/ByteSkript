/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.loop;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.WhileTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;

@Documentation(
    name = "While Loop",
    description = """
        Re-runs this loop as long as the value given is true.
        """,
    examples = {
        """
            while {number} is 3:
                set {number} to 6
            while {@var} is true:
                print "hello"
                    """
    }
)
public class EffectWhileSection extends Section {
    
    public EffectWhileSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "while %Boolean%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("while ")) return null;
        return super.match(thing, context);
    }

    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }

    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final WhileTree tree = new WhileTree(context.getSection(1));
        context.createTree(tree);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label top = new Label();
        tree.setTop(top);
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof WhileTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label end = tree.getEnd().use();
        method.writeCode(WriteInstruction.invokeVirtual(Boolean.class.getMethod("booleanValue")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, end));
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
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof WhileTree found) current = found;
        else current = context.getCurrentTree();
        if (!(current instanceof WhileTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance while flow tree.");
        context.setState(CompileState.CODE_BODY);
        tree.close(context);
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
    
    @Override
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        final WhileTree tree = new WhileTree(context.getSection());
        context.createTree(tree);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Label top = new Label();
        tree.setTop(top);
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
    }
    
}
