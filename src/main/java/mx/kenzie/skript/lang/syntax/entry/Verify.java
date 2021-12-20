package mx.kenzie.skript.lang.syntax.entry;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.api.syntax.TriggerHolder;
import mx.kenzie.skript.compiler.CompileState;
import mx.kenzie.skript.compiler.Context;
import mx.kenzie.skript.compiler.Pattern;
import mx.kenzie.skript.compiler.SkriptLangSpec;
import mx.kenzie.skript.compiler.structure.PreVariable;
import mx.kenzie.skript.compiler.structure.SectionMeta;
import mx.kenzie.skript.compiler.structure.VerifyTree;
import mx.kenzie.skript.lang.element.StandardElements;

import java.lang.reflect.Modifier;

public class Verify extends Section {
    
    public Verify() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "verify");
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final MethodBuilder target = context.getMethod();
        final VerifyTree tree = new VerifyTree(context.getSection(1), target, context.getVariables());
        context.createTree(tree);
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getBuilder()
            .addMethod(target.getErasure().name() + "_verify")
            .addModifiers(Modifier.STATIC)
            .addParameter(target.getErasure().parameterTypes())
            .setReturnType(Object.class);
        method.writeCode(prepareVariables(tree));
        context.setMethod(method);
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
    
}
