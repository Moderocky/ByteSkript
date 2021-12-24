package org.byteskript.skript.compiler.structure;

import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;
import org.objectweb.asm.Label;

public abstract class ProgrammaticSplitTree {
    
    public abstract SectionMeta owner();
    
    public abstract MultiLabel getEnd();
    
    public Label getNext() {
        return getEnd().use();
    }
    
    public abstract void start(Context context);
    
    public abstract void branch(Context context);
    
    public abstract void close(Context context);
    
    public abstract boolean permit(SyntaxElement element);
    
    public abstract boolean isOpen();
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + hashCode() + "]";
    }
}
