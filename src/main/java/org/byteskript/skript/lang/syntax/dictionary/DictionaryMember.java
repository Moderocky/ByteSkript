/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.dictionary;

import mx.kenzie.foundation.MethodBuilder;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Member;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;

@Documentation(
    name = "Dictionary",
    description = """
        Tells the compiler about available resources in other files.
        Allows remote types and functions to be 'imported' to avoid using their fully-qualified names in code.
        """,
    examples = {
        """
            dictionary:
                import type skript/otherscript/MyType
                import function "cool_func" from skript/otherscript
            """
    }
)
public class DictionaryMember extends Member {
    
    public DictionaryMember() {
        super(SkriptLangSpec.LIBRARY, StandardElements.MEMBER, "dictionary");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("dictionary")) return null;
        if (context.hasFlag(AreaFlag.IN_TYPE)) {
            context.getError().addHint(this, "The dictionary must be a root-level element.");
            return null;
        }
        return super.match(thing, context);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        context.addFlag(AreaFlag.IN_DICTIONARY);
        context.setMethod(new MethodBuilder(null, "<dict>"));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.removeFlag(AreaFlag.IN_DICTIONARY);
        context.closeAllTrees();
        super.onSectionExit(context, meta);
    }
    
}
