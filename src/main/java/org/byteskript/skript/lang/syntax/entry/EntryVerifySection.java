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
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.api.syntax.TriggerHolder;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.VerifyTree;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.Modifier;

@Documentation(
    name = "Verifier",
    description = """
        A test-scenario for a function, checked at load.
        This can be used to check whether libraries, etc. are available.
        """,
    examples = {
        """
            function my_func:
                verify:
                    assert "hello" is a string
                    """,
    }
)
public class EntryVerifySection extends Section {
    
    public EntryVerifySection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "verify");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder target = context.getMethod();
        final VerifyTree tree = new VerifyTree(context.getSection(1), target, context.getVariables());
        context.createTree(tree);
        context.addFlag(AreaFlag.IN_TRIGGER); // technically a trigger section
        context.addFlag(AreaFlag.IN_VERIFIER);
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getBuilder()
            .addMethod(target.getErasure().name() + "_verify")
            .addModifiers(Modifier.STATIC)
            .addParameter(target.getErasure().parameterTypes())
            .setReturnType(Object.class);
        method.writeCode(prepareVariables(tree));
        context.setMethod(method, true);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.getParent() instanceof TriggerHolder;
    }
    
    private WriteInstruction prepareVariables(VerifyTree context) {
        return (writer, visitor) -> {
            int i = 0;
            for (PreVariable variable : context.getVariables()) {
                if (!variable.skipPreset()) {
                    visitor.visitInsn(1);
                    visitor.visitVarInsn(58, i);
                }
                i++;
            }
        };
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
        context.removeFlag(AreaFlag.IN_VERIFIER);
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
