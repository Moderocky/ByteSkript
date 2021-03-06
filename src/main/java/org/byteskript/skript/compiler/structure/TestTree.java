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

public class TestTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end = new MultiLabel();
    private final Label next = new Label();
    private boolean open;
    
    public TestTree(SectionMeta owner) {
        this.owner = owner;
        this.open = true;
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
    
}
