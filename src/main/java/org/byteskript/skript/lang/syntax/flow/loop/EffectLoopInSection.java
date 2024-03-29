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
import org.byteskript.skript.compiler.structure.LoopTree;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.variable.ExprVariable;
import org.byteskript.skript.runtime.internal.OperatorHandler;
import org.objectweb.asm.Label;

import java.util.Iterator;

@Documentation(
    name = "Loop Objects",
    description = """
        Loops through the contents of the second value.
        The loop-value will be stored in the variable in the first slot.
        """,
    examples = {
        """
            loop {number} in (1, 2, 3):
                print {number}
                    """
    }
)
public class EffectLoopInSection extends Section {
    
    public EffectLoopInSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "loop %Variable% in %Object%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("loop ")) return null;
        if (!thing.contains(" in ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getCompileCurrent().nested()[0];
        if (!(holder.current() instanceof ExprVariable))
            throw new ScriptParseError(context.lineNumber(), "The extracted element must be a variable.");
        holder.type = StandardHandlers.SET;
        holder.compile = false;
        final LoopTree tree = new LoopTree(context.getSection(1));
        context.createTree(tree);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        this.compileTogether(context, tree, match);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    private void compileTogether(Context context, LoopTree tree, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final PreVariable variable = new PreVariable(null);
        variable.internal = true;
        context.forceUnspecVariable(variable);
        final int slot = context.slotOf(variable);
        this.writeCall(method, OperatorHandler.class.getMethod("acquireIterator", Object.class), context);
        method.writeCode(WriteInstruction.storeObject(slot));
        final Label top = new Label();
        final Label end = tree.getEnd().use();
        tree.setTop(top);
        tree.slot = slot;
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
        method.writeCode(WriteInstruction.loadObject(slot));
        method.writeCode(WriteInstruction.invokeInterface(Iterator.class.getMethod("hasNext")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, end));
        method.writeCode(WriteInstruction.loadObject(slot));
        method.writeCode(WriteInstruction.invokeInterface(Iterator.class.getMethod("next")));
        {
            final ElementTree holder = context.getCompileCurrent().nested()[0];
            holder.type = StandardHandlers.SET;
            holder.compile = true;
            holder.preCompile(context);
            holder.compile(context);
        }
        context.setState(CompileState.CODE_BODY);
    }
    
    private PreVariable getHolderVariable(Context context, Pattern.Match match) {
        final String pattern = match.groups()[0].trim();
        final String name = pattern.substring(1, pattern.length() - 1);
        return context.getVariable(name);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof LoopTree found) current = found;
        else current = context.getCurrentTree();
        if (current instanceof LoopTree tree) {
            tree.close(context);
            context.setState(CompileState.CODE_BODY);
        }
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection()) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        this.compileTogether(context, tree, match);
        tree.close(context);
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getCompileCurrent().nested()[0];
        holder.type = StandardHandlers.SET;
        holder.compile = false;
        final LoopTree tree = new LoopTree(context.getSection());
        context.createTree(tree);
    }
    
}
