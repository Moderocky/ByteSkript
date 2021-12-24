package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseIfSection;
import org.byteskript.skript.lang.syntax.flow.conditional.ElseSection;
import org.objectweb.asm.Label;

public class ExtractionTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private boolean open;
    private Label next;
    private final MethodBuilder parent;
    private final MultiLabel end;
    
    public ExtractionTree(SectionMeta owner, MethodBuilder parent, MultiLabel end) {
        this.owner = owner;
        this.parent = parent;
        this.open = true;
        this.end = end;
    }
    
    public MethodBuilder getParent() {
        return parent;
    }
    
    public MultiLabel getEnd() {
        return end;
    }
    
    public Label getNext() {
        return next;
    }
    
    public void setNext(Label next) {
        this.next = next;
    }
    
    @Override
    public SectionMeta owner() {
        return owner;
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
        return element instanceof ElseIfSection || element instanceof ElseSection;
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
}
