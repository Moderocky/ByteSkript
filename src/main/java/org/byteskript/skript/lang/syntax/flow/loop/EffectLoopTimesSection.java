/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.loop;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.LoopTree;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

@Documentation(
    name = "Loop Times",
    description = """
        Loops the given number of times.
        """,
    examples = {
        """
            loop {number} times:
                print "hello"
                    """
    }
)
public class EffectLoopTimesSection extends Section {
    
    public EffectLoopTimesSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "loop %Number% times");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("loop ")) return null;
        if (!thing.endsWith(" times")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final LoopTree tree = new LoopTree(context.getSection(1));
        context.createTree(tree);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.cast(CommonTypes.NUMBER));
        method.writeCode(WriteInstruction.invokeVirtual(Number.class.getMethod("intValue")));
        final PreVariable variable = new PreVariable(null);
        variable.internal = true;
        context.forceUnspecVariable(variable);
        final int slot = context.slotOf(variable);
        final Label top = new Label();
        final Label end = tree.getEnd().use();
        tree.setTop(top);
        tree.slot = slot;
        method.writeCode(WriteInstruction.storeSmall(slot)); // store loop number
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
        method.writeCode(WriteInstruction.loadSmall(slot));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(Opcodes.IFLE, end));
        method.writeCode(WriteInstruction.incrementSmall(slot, -1));
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
        if (context.getTree(context.getSection()) instanceof LoopTree found) current = found;
        else current = context.getCurrentTree();
        if (!(current instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance loop flow tree.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        final Label top = tree.getTop();
        final int slot = tree.slot;
        method.writeCode(WriteInstruction.loadSmall(slot));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(Opcodes.IFGT, top));
        tree.close(context);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection()) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.cast(CommonTypes.NUMBER));
        method.writeCode(WriteInstruction.invokeVirtual(Number.class.getMethod("intValue")));
        final PreVariable variable = new PreVariable(null);
        variable.internal = true;
        context.forceUnspecVariable(variable);
        final int slot = context.slotOf(variable);
        final Label top = new Label();
        tree.setTop(top);
        tree.slot = slot;
        method.writeCode(WriteInstruction.storeSmall(slot)); // store loop number
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
        method.writeCode(WriteInstruction.incrementSmall(slot, -1));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        final LoopTree tree = new LoopTree(context.getSection());
        context.createTree(tree);
    }
    
}
