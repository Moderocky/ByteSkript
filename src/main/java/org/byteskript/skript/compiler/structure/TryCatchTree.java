/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.syntax.flow.error.CatchSection;
import org.objectweb.asm.Label;

public class TryCatchTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private boolean open;
    private final Label startTry = new Label();
    private final Label startCatch = new Label();
    private final MultiLabel end;
    
    public TryCatchTree(SectionMeta owner, MultiLabel end) {
        this.owner = owner;
        this.open = true;
        this.end = end;
    }
    
    public Label getStartTry() {
        return startTry;
    }
    
    public Label getStartCatch() {
        return startCatch;
    }
    
    public MultiLabel getEnd() {
        return end;
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
    }
    
    @Override
    public void start(Context context) {
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Try/catch used outside method.");
        method.writeCode((writer, visitor) -> {
            visitor.visitTryCatchBlock(startTry, end.use(), startCatch, CommonTypes.THROWABLE.internalName());
            visitor.visitLabel(startTry);
        });
    }
    
    @Override
    public void branch(Context context) {
    }
    
    @Override
    public void close(Context context) {
        this.open = false;
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Try/catch section left unclosed.");
        method.writeCode(end.instruction());
        context.removeTree(this);
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return element instanceof CatchSection;
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
}
