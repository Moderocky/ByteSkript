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

public class PropertyEntry extends SimpleEntry {
    
    public PropertyEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.METADATA, "property: %Pattern%");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        final String pattern = (String) match.meta();
        final SyntaxTree tree = ((SyntaxTree) context.getCurrentTree());
        tree.addHandler(new SyntaxTree.Handler(SyntaxTree.Type.PROPERTY, pattern));
        context.setState(CompileState.MEMBER_BODY);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("property: ")) return null;
        final String raw = thing.substring(10).trim();
        if (raw.isEmpty()) throw new ScriptCompileError(context.lineNumber(), "No pattern was specified.");
        if (thing.contains("\""))
            throw new ScriptCompileError(context.lineNumber(), "Patterns should not contain quotation marks.");
        return new Pattern.Match(Pattern.fakeMatcher(thing), raw);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context) && context.hasFlag(AreaFlag.IN_SYNTAX);
    }
    
    
}
