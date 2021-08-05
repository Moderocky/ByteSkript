package mx.kenzie.skript.compiler.structure;

import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.SectionMeta;

public class TriggerTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end;
    
    public TriggerTree(SectionMeta owner) {
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
    
    }
    
    @Override
    public void branch(Context context) {
    
    }
    
    @Override
    public void close(Context context) {
        if (end.uses.size() > 0)
            context.getMethod().writeCode(end.instruction());
    }
    
    @Override
    public boolean permit(SyntaxElement element) {
        return false;
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
}
