/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.RewriteController;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.SyntaxElement;
import org.byteskript.skript.compiler.structure.MultiLabel;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.lang.syntax.variable.VariableExpression;
import org.objectweb.asm.Label;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InlineController extends RewriteController {
    
    protected final Map<Integer, PreVariable> special = new HashMap<>();
    final Context context;
    final MultiLabel label = new MultiLabel();
    int returnCount;
    
    public InlineController(Context context, Method method) {
        this.context = context;
        this.prepareVariables(method);
    }
    
    private void prepareVariables(Method method) {
        final ElementTree[] inputs = context.getCompileCurrent().nested();
        for (int i = 0; i < inputs.length; i++) {
            final SyntaxElement element = inputs[i].current();
            if (element.getClass() != VariableExpression.class) continue;
            final VariableExpression expression = (VariableExpression) inputs[i].current();
            special.put(i, expression.getVariable(context, inputs[i].match()));
        }
        final MethodBuilder builder = context.getMethod();
        for (int i = method.getParameterTypes().length - 1; i >= 0; i--) {
            if (special.containsKey(i)) {
                builder.writeCode(WriteInstruction.pop()); // have to pop, aload is already queued
                continue;
            }
            final PreVariable var = new PreVariable("$unspec_" + i);
            context.forceUnspecVariable(var);
            final int slot = context.slotOf(var);
            builder.writeCode(WriteInstruction.storeObject(slot));
        }
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
        if (special.containsKey(i)) return context.slotOf(special.get(i));
        final PreVariable variable = new PreVariable("$unspec_" + i);
        special.put(i, variable);
        context.forceUnspecVariable(variable);
        return context.slotOf(variable);
    }
    
    @Override
    public WriteInstruction return0(int opcode) {
        return this.jumpToEnd();
//        if (opcode < 177) {
//            int slot = this.returnSlot();
//            return (writer, visitor) -> {
//                visitor.visitVarInsn(opcode - 118, slot);
//                this.jumpToEnd().accept(writer, visitor);
//            };
//        } else {
//            return this.jumpToEnd();
//        }
    }
    
    @Override
    public int returnSlot() {
        final PreVariable variable = new PreVariable("$unspec_ret");
        context.forceUnspecVariable(variable);
        return context.slotOf(variable);
    }
    
    @Override
    public void write(String s, int i, Object o) {
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
