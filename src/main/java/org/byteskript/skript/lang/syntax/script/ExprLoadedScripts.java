/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.script;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.internal.ExtractedSyntaxCalls;

@Documentation(
    name = "Loaded Scripts",
    description = """
        A list of the main classes for all loaded scripts.
        Storing these will prevent them being unloaded safely.
        """,
    examples = {
        """
            set {list} to the loaded scripts
            loop {script} in {list}:
                print name of {script}
                """
    }
)
public class ExprLoadedScripts extends SimpleExpression {
    
    public ExprLoadedScripts() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[(the|all)] loaded scripts");
        handlers.put(StandardHandlers.GET, findMethod(ExtractedSyntaxCalls.class, "getLoadedScripts"));
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.LIST;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.LIST.equals(type) || super.allowAsInputFor(type);
    }
    
}
