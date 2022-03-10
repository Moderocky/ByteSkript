/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.error.ScriptCompileError;
import org.objectweb.asm.Label;

public class ExtractionTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MethodBuilder parent;
    private final MultiLabel end;
    private boolean open;
    private Label next;
    
    public ExtractionTree(SectionMeta owner, MethodBuilder parent, MultiLabel end) {
        this.owner = owner;
        this.parent = parent;
        this.open = true;
        this.end = end;
    }
    
    public MethodBuilder getParent() {
        return parent;
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
    }
    
    public Label getNext() {
        return next;
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
        this.open = false;
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Extraction tree left unclosed.");
        method.writeCode(end.instruction());
        context.removeTree(this);
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return false;
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    public void setNext(Label next) {
        this.next = next;
    }
    
}
