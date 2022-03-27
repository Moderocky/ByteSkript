/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.error.ScriptCompileError;
import org.objectweb.asm.Label;

public class MonitorTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end;
    public int slot;
    protected boolean closed;
    
    public MonitorTree(SectionMeta owner) {
        this.owner = owner;
        this.end = new MultiLabel();
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
    }
    
    @Override
    public MultiLabel getEnd() {
        return end;
    }
    
    @Override
    public void start(Context context) {
        this.closed = false;
        final PreVariable variable = new PreVariable(null);
        context.forceUnspecVariable(variable);
        this.slot = context.slotOf(variable);
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.duplicate());
        method.writeCode(WriteInstruction.storeObject(slot));
        method.writeCode((writer, visitor) -> visitor.visitInsn(194));
    }
    
    @Override
    public void branch(Context context) {
    
    }
    
    @Override
    public void close(Context context) {
        this.closed = true;
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Monitor block left unclosed.");
        final Label label = end.use();
        method.writeCode(WriteInstruction.label(label));
        method.writeCode(WriteInstruction.loadObject(slot));
        method.writeCode((writer, visitor) -> visitor.visitInsn(195));
        context.removeTree(this);
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return !closed;
    }
}
