/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.config;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.note.ForceInline;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.config.ConfigMap;

@Documentation(
    name = "New Config",
    description = """
        A new key/value config.
        This functions like a map and can be written to a file.
        """,
    examples = {
        """
            set {config} to a new config
            set "blob" in {map} to 55.3
                """
    }
)
public class ConfigCreator extends SimpleExpression {
    
    public ConfigCreator() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[a] new config[uration]");
        handlers.put(StandardHandlers.GET, findMethod(this.getClass(), "create"));
    }
    
    @ForceInline
    public static ConfigMap create() {
        return new ConfigMap();
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.CONFIG;
    }
    
}
