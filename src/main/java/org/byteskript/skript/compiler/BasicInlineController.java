/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.RewriteController;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.objectweb.asm.Label;

import java.util.HashMap;
import java.util.Map;

public class BasicInlineController extends RewriteController {
    
    protected final Map<Integer, PreVariable> special = new HashMap<>();
    final Context context;
    final MultiLabel label = new MultiLabel();
    int returnCount;
    
    public BasicInlineController(Context context) {
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
    public int adjustVariable(int i) {
        System.out.println(i);
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
    public void write(String s, int i, Object o) {
    }
    
    @Override
    public WriteInstruction end() {
        return label.instruction();
    }
}
