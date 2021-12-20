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
import mx.kenzie.skript.compiler.structure.TriggerTree;
import mx.kenzie.skript.lang.element.StandardElements;

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
    
    private WriteInstruction prepareVariables(TriggerTree context) {
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
