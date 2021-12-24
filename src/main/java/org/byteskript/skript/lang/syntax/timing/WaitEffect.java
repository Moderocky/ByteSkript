/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.timing;

import org.byteskript.skript.api.note.ForceExtract;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.time.Duration;

public class WaitEffect extends Effect {
    
    public WaitEffect() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EFFECT, "wait[ for] %Duration%");
        try {
            handlers.put(StandardHandlers.RUN, this.getClass().getMethod("run", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("wait ")) return null;
        return super.match(thing, context);
    }
    
    @ForceExtract
    public static void run(Object object) {
        if (!(object instanceof Duration duration))
            throw new ScriptRuntimeError("Wait effect requires duration.");
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            throw new ScriptRuntimeError("Wait effect interrupted.", ex);
        }
    }
}