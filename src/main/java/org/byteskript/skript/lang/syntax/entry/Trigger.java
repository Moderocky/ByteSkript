/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SectionEntry;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.TriggerTree;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.type.AtomicVariable;

@Documentation(
    name = "Trigger",
    description = "The code-holding section for a function, event or other member.",
    examples = {
        """
            function my_func:
                trigger:
                    print "hello" // inside the trigger.
                    """,
        """
            on load:
                trigger:
                    print "hello" // inside the trigger.
                    """
    }
)
public class Trigger extends SectionEntry {
    
    private final WriteInstruction wrap = WriteInstruction
        .invokeStatic(new Type(AtomicVariable.class), new Type(AtomicVariable.class), "wrap", CommonTypes.OBJECT);
    
    public Trigger() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "trigger");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final TriggerTree tree = new TriggerTree(context.getSection(1), context.getVariables());
        context.createTree(tree);
        context.addFlag(AreaFlag.IN_TRIGGER);
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        method.removeModifiers(0x0400); // not abstract
        method.writeCode(prepareVariables(tree));
    }
    
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
                    // this is handled by the dynamic callsite now
                }
                i++;
            }
        };
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
        context.removeFlag(AreaFlag.IN_TRIGGER);
        context.setState(CompileState.MEMBER_BODY);
    }
    
}
