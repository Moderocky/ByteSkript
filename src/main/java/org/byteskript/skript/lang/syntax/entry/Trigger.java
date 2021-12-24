package org.byteskript.skript.lang.syntax.entry;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.TriggerTree;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.type.AtomicVariable;
import org.objectweb.asm.Opcodes;

public class Trigger extends Section {
    
    public Trigger() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "trigger");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final TriggerTree tree = new TriggerTree(context.getSection(1), context.getVariables());
        context.createTree(tree);
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        method.writeCode(prepareVariables(tree));
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.getParent() instanceof TriggerHolder;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        context.closeAllTrees();
        if (method.getErasure().returnType().equals(new Type(void.class))) {
            method.writeCode(WriteInstruction.returnEmpty());
        } else {
            method.writeCode(WriteInstruction.pushNull());
            method.writeCode(WriteInstruction.returnObject());
        }
        context.emptyVariables();
        context.setState(CompileState.MEMBER_BODY);
    }
    
    private final WriteInstruction wrap = WriteInstruction
        .invokeStatic(new Type(AtomicVariable.class), new Type(AtomicVariable.class), "wrap", CommonTypes.OBJECT);
    private final WriteInstruction unwrap = WriteInstruction
        .invokeStatic(new Type(AtomicVariable.class), CommonTypes.OBJECT, "unwrap", CommonTypes.OBJECT);
    
    private WriteInstruction prepareVariables(TriggerTree context) {
        return (writer, visitor) -> {
            int i = 0;
            for (PreVariable variable : context.getVariables()) {
                if (!variable.skipPreset()) {
                    if (variable.atomic) {
                        visitor.visitInsn(1); // push null
                        wrap.accept(writer, visitor);
                        visitor.visitVarInsn(58, i); // astore
                    } else {
                        visitor.visitInsn(1); // push null
                        visitor.visitVarInsn(58, i); // astore
                    }
                }
                if (variable.parameter) {
                    if (variable.atomic) {
                        visitor.visitVarInsn(Opcodes.ALOAD, i);
                        wrap.accept(writer, visitor);
                        visitor.visitVarInsn(Opcodes.ASTORE, i);
                    } else {
                        visitor.visitVarInsn(Opcodes.ALOAD, i);
                        unwrap.accept(writer, visitor);
                        visitor.visitVarInsn(Opcodes.ASTORE, i);
                    }
                }
                i++;
            }
        };
    }
    
}
