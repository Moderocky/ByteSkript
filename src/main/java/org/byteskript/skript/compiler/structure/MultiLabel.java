/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler.structure;

import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.List;

public class MultiLabel {
    
    protected final List<Label> uses = new ArrayList<>();
    
    public Label use() {
        final Label label;
        this.uses.add(label = new Label());
        return label;
    }
    
    public WriteInstruction instruction() {
        return (writer, method) -> {
            for (final Label use : uses) {
                method.visitLabel(use);
            }
        };
    }
    
}
