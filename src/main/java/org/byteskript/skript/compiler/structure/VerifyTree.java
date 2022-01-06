/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.Context;

import java.util.Collection;

public class VerifyTree extends ProgrammaticSplitTree {
    
    private final SectionMeta owner;
    private final MultiLabel end;
    private final MethodBuilder method;
    private final Collection<PreVariable> variables;
    
    public VerifyTree(SectionMeta owner, MethodBuilder method, Collection<PreVariable> variables) {
        this.owner = owner;
        this.end = new MultiLabel();
        this.method = method;
        this.variables = variables;
    }
    
    public Collection<PreVariable> getVariables() {
        return variables;
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
    
    public MethodBuilder getMethod() {
        return method;
    }
}
