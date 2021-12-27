/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry.syntax;

import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.syntax.SimpleEntry;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SyntaxTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.Locale;

public class ModeEntry extends SimpleEntry {
    
    public ModeEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "mode: %Mode%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String mode = (String) match.meta();
        final SyntaxTree tree = ((SyntaxTree) context.getCurrentTree());
        tree.mode = StandardHandlers.valueOf(mode.toUpperCase(Locale.ROOT));
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("mode: ")) return null;
        final String mode = thing.substring(6).trim();
        if (mode.isEmpty()) throw new ScriptCompileError(context.lineNumber(), "No mode was specified.");
        if (thing.contains("\"") || mode.contains(" "))
            throw new ScriptCompileError(context.lineNumber(), "Modes are set, get, add, remove or delete.");
        return new Pattern.Match(Pattern.fakeMatcher(thing), mode);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.hasFlag(AreaFlag.IN_SYNTAX);
    }
    
    
}
