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
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "save config %Object%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.getField(System.class.getField("out")));
        super.preCompile(context, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.cast(CommonTypes.CONFIG));
        method.writeCode(WriteInstruction.invokeVirtual(ConfigMap.class.getMethod("save")));
        context.setState(CompileState.CODE_BODY);
    }
    
}
