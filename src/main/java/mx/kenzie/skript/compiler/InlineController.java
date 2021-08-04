package mx.kenzie.skript.compiler;

import mx.kenzie.foundation.RewriteController;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.skript.compiler.structure.MultiLabel;
import mx.kenzie.skript.compiler.structure.PreVariable;
import org.objectweb.asm.Label;

import java.util.HashMap;
import java.util.Map;

public class InlineController extends RewriteController {
    
    final Context context;
    int returnCount;
    final MultiLabel label = new MultiLabel();
    protected final Map<Integer, PreVariable> special = new HashMap<>();
    
    public InlineController(Context context) {
        this.context = context;
    }
    
    public Map<Integer, PreVariable> getSpecial() {
        return special;
    }
    
    @Override
    public boolean isInline() {
        return true;
    }
    
    @Override
    public void markReturn() {
        returnCount++;
    }
    
    @Override
    public void useField(Type type, String s) {
    }
    
    @Override
    public void useMethod(Type type, String s) {
    
    }
    
    @Override
    public void write(String s, int i, Object o) {
    }
    
    @Override
    public int adjustVariable(int i) {
        if (special.containsKey(i)) return context.slotOf(special.get(i));
        final PreVariable variable = new PreVariable("$unspec_" + i);
        special.put(i, variable);
        context.forceUnspecVariable(variable);
        return context.slotOf(variable);
    }
    
    @Override
    public int returnSlot() {
        final PreVariable variable = new PreVariable("$unspec_ret");
        context.forceUnspecVariable(variable);
        return context.slotOf(variable);
    }
    
    @Override
    public WriteInstruction jumpToEnd() {
        final Label to = label.use();
        return (writer, method) -> method.visitJumpInsn(167, to);
    }
    
    @Override
    public WriteInstruction end() {
        return label.instruction();
    }
}
