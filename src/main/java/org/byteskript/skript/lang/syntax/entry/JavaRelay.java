/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.FunctionalEntrySection;
import org.byteskript.skript.api.note.EntryNode;
import org.byteskript.skript.api.note.EntrySection;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;

import static mx.kenzie.foundation.WriteInstruction.*;

@EntrySection("java method target")
public record JavaRelay(
    @EntryNode("owner: %String%") String owner,
    @EntryNode("name: %String%") String name,
    @EntryNode("descriptor: %String%") String descriptor
) implements FunctionalEntrySection {
    
    @Override
    public void compile(Context context) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.addModifiers(0x00000040);
        final MethodErasure target = MethodErasure.of(name, descriptor);
        final Type owner = Type.of(owner());
        final boolean writeTypes = method.getErasure().parameterTypes().length == 0;
        int index = 0;
        for (Type type : target.parameterTypes()) {
            if (writeTypes) method.addParameter(CommonTypes.OBJECT);
            method.writeCode(loadObject(index));
            method.writeCode(cast(type));
            index++;
        }
        method.writeCode(invokeStatic(owner, target));
        if (target.returnType().equals(new Type(void.class))) method.writeCode(returnEmpty());
        else method.writeCode(returnObject());
    }
    
}
