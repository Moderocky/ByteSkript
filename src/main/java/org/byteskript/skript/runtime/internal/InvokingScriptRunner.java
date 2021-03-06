/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.runtime.internal;

import mx.kenzie.autodoc.api.note.Ignore;
import org.byteskript.skript.runtime.threading.ScriptRunner;

@Ignore
public record InvokingScriptRunner(Class<? extends CompiledScript> owner, Member method,
                                   Object... parameters) implements ScriptRunner {
    
    @Override
    public void start() {
        method.invoke(parameters);
    }
}
