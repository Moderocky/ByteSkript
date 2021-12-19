package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.Context;

public class VerifyTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end;
    private final MethodBuilder method;
    
    public VerifyTree(SectionMeta owner, MethodBuilder method) {
        this.owner = owner;
        this.end = new MultiLabel();
        this.method = method;
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
    
    public MethodBuilder getMethod() {
        return method;
    }
    
    @Override
    public void close(Context context) {
        if (end.uses.size() > 0)
            context.getMethod().writeCode(end.instruction());
        context.setMethod(method);
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
