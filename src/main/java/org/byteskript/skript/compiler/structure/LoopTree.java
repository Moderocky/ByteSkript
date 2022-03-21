/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseIfSection;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseSection;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class LoopTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end;
    public int slot;
    private boolean open;
    private Label top;
    
    public LoopTree(SectionMeta owner) {
        this.owner = owner;
        this.open = true;
        this.end = new MultiLabel();
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
    }
    
    public MultiLabel getEnd() {
        return end;
    }
    
    @Override
    public void start(Context context) {
    
    }
    
    @Override
    public void branch(Context context) {
    
    }
    
    @Override
    public void close(Context context) {
        final MethodBuilder method = context.getMethod();
        final Label top = this.getTop();
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(Opcodes.GOTO, top));
        this.open = false;
        method.writeCode(end.instruction());
        context.removeTree(this);
    }
    
    public Label getTop() {
        return top;
    }
    
    public void setTop(Label next) {
        this.top = next;
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return element instanceof ElseIfSection || element instanceof ElseSection;
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
}
