package mx.kenzie.skript.compiler.structure;

import mx.kenzie.foundation.CodeWriter;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.api.SyntaxElement;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.SectionMeta;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class ProgrammaticSplitTree {
    
    public abstract SectionMeta owner();
    
    public abstract MultiLabel getEnd();
    
    public abstract void start(Context context);
    
    public abstract void branch(Context context);
    
    public abstract void close(Context context);
    
    public abstract boolean permit(SyntaxElement element);
    
    public abstract boolean isOpen();
    
    public static class ClosingJump implements WriteInstruction {
        
        public WriteInstruction jump;
        public Label end;
        
        @Override
        public void accept(CodeWriter codeWriter, MethodVisitor methodVisitor) {
            if (jump != null) jump.accept(codeWriter, methodVisitor);
            methodVisitor.visitLabel(end);
        }
    }
    
}
