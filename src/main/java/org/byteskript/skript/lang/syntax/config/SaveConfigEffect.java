/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.config;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.runtime.config.ConfigMap;

import java.util.Map;

@Documentation(
    name = "Save Config",
    description = """
        Saves the given config object.
        """,
    examples = {
        """
            set {conf} to my/config.csk
            save config {conf}
            """
    }
)
public class SaveConfigEffect extends Effect {
    
    public SaveConfigEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "save config %Object% to %Object%", "save config %Object%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        if (match.matchedPattern == 1) {
            method.writeCode(WriteInstruction.cast(CommonTypes.CONFIG));
            method.writeCode(WriteInstruction.invokeVirtual(ConfigMap.class.getMethod("save")));
        } else {
            method.writeCode(WriteInstruction.cast(CommonTypes.CONFIG)); // orig, new
            method.writeCode(WriteInstruction.duplicateDrop2()); // new, orig, new
            method.writeCode(WriteInstruction.swap());  // new, new, orig
            method.writeCode(WriteInstruction.cast(CommonTypes.CONFIG)); // new, new, orig
            method.writeCode(WriteInstruction.invokeVirtual(ConfigMap.class.getMethod("putAll", Map.class))); // new
            method.writeCode(WriteInstruction.invokeVirtual(ConfigMap.class.getMethod("save")));
        }
        context.setState(CompileState.CODE_BODY);
    }
    
}
