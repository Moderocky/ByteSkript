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
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.getParent() instanceof TriggerHolder;
    }
    
    @Override
    public void onSectionExit(Context context) {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        method.writeCode(WriteInstruction.pushNull());
        method.writeCode(WriteInstruction.returnObject());
        context.setState(CompileState.MEMBER_BODY);
    }
}
