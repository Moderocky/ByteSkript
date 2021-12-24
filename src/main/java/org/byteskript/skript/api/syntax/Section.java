/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api.syntax;

import mx.kenzie.foundation.Type;
import org.byteskript.skript.api.LanguageElement;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;

public abstract class Section extends Element {
    public Section(Library provider, LanguageElement type, String... patterns) {
        super(provider, type, patterns);
    }
    
    
    public abstract void onSectionExit(Context context, SectionMeta meta);
    
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'" + name() + "' must be used as a section-header.");
    }
    
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        // Rarely used
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return false;
    }
    
}
